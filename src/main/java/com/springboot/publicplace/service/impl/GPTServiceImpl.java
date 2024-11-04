package com.springboot.publicplace.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.publicplace.controller.GPTController;
import com.springboot.publicplace.dto.request.GPTRequest;
import com.springboot.publicplace.dto.response.GPTResponse;
import com.springboot.publicplace.dto.response.GPTTeamListDto;
import com.springboot.publicplace.entity.Message;
import com.springboot.publicplace.service.GPTService;
import com.springboot.publicplace.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GPTServiceImpl implements GPTService {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    private final RestTemplate template;
    private static final Logger LOGGER = LoggerFactory.getLogger(GPTServiceImpl.class);

    private final TeamService teamService;  // TeamService 추가
    private final ObjectMapper objectMapper; // ObjectMapper 추가

    // 시스템 프롬프트 정의 (프롬프트 엔지니어링)
    private static final String SYSTEM_PROMPT =
            "너는 조기축구 팀을 추천해주는 챗봇이야. 사용자가 원하는 조건에 맞춰 적절한 팀을 최대한 자세하고 친절하게 추천해줘. " +
                    "주어진 팀들의 정보를 모두 기억하고, 사용자가 조건을 제시하면 그에 맞는 팀을 하나도 빠짐없이 알려줄 수 있도록 해. " +
                    "만약 팀이 여러 개라면, 각 팀의 상세 정보를 하나씩 설명하고, 사용자가 이해하기 쉽게 간결하게 정리해줘. " +
                    "응답이 느려도 괜찮으니, 친절하게 자세하게 응답해. " +

                    "답변을 할 때는 다음 원칙을 따라: " +
                    "1. **사용자의 요청을 정확히 이해**하고, 필요한 정보를 추출해. 예를 들어, '용인에서 활동하는 모든 팀'이라는 요청이 오면, " +
                    "주어진 팀 목록을 모두 탐색해 teamLocation에 '용인'이 포함된 팀을 하나씩 확인해서 알려줘." +
                    "2. 사용자가 추가 질문을 했을 때 빠르게 대처할 수 있도록, 팀 목록을 잘 기억하고 사용자의 조건에 따라 맞춤형 답변을 제공해. " +
                    "3. **친절하고 상냥한 어투**로 답변하며, 사용자에게 필요한 팀의 정보는 자세히 제공하되, 너무 복잡하지 않게 요점을 잘 정리해." +

                    "예시 응답: " +
                    "- '용인에서 활동하는 모든 팀 알려줘'라는 요청이 오면, 팀 목록을 탐색해서 용인에서 활동하는 팀들을 차례로 소개해. " +
                    "각 팀의 이름, 활동 지역, 회원 수, 연령대, 창단일, 팀 특성을 하나씩 자세하게 알려줘. " +
                    "마지막에는 '추가로 궁금하신 점이 있으시면 언제든지 말씀해 주세요!'라는 문구를 덧붙여서 친절하게 마무리해." +

                    "모든 팀의 정보를 충실하게 기억하고, 사용자에게 필요할 때 정확한 정보를 제공할 수 있도록 항상 주의해줘.";

    @Override
    public List<Message> getChatResponses(HttpServletRequest servletRequest, String prompt, HttpSession session) {

        // 팀 목록을 가져오는 요청
        List<GPTTeamListDto> teamList = teamService.getGptTeamList(servletRequest);

        // 팀 정보를 JSON 문자열로 변환
        String teamJson;
        try {
            teamJson = objectMapper.writeValueAsString(teamList);
        } catch (Exception e) {
            LOGGER.error("팀 정보를 JSON으로 변환하는데 실패했습니다.", e);
            throw new RuntimeException("팀 정보 변환 오류");
        }

        // 시스템 프롬프트 업데이트
        String updatedSystemPrompt = SYSTEM_PROMPT + " 주어진 팀 목록: " + teamJson;

        // 세션에서 이전 대화 내용 가져오기
        List<Message> messages = (List<Message>) session.getAttribute("messages");
        if (messages == null) {
            messages = new ArrayList<>();
            // 시스템 프롬프트 추가
            messages.add(new Message("system", updatedSystemPrompt));
        } else {
            // 기존 메시지에서 시스템 프롬프트를 업데이트
            messages.set(0, new Message("system", updatedSystemPrompt));
        }

        // 로그에 현재 메시지 내용 출력
        LOGGER.info("Current messages: {}", messages);

        // 사용자 메시지 추가
        messages.add(new Message("user", prompt));

        // 요청 생성
        GPTRequest request = new GPTRequest(model, messages);

        // API 호출
        GPTResponse chatGPTResponse = template.postForObject(apiURL, request, GPTResponse.class);
        if (chatGPTResponse == null || chatGPTResponse.getChoices() == null || chatGPTResponse.getChoices().isEmpty()) {
            LOGGER.error("Invalid response from GPT API");
            throw new RuntimeException("GPT API 응답이 유효하지 않습니다.");
        }

        String responseContent = chatGPTResponse.getChoices().get(0).getMessage().getContent();

        // 어시스턴트 응답 메시지 추가
        messages.add(new Message("assistant", responseContent));

        // 세션에 대화 내용 저장
        session.setAttribute("messages", messages);

        return messages;
    }
}