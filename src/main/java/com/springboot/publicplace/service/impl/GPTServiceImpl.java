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
    private static final String SYSTEM_PROMPT = "너는 조기축구 팀을 추천해주는 챗봇이야 주어진 팀들의 대한 정보로만 상냥하게 대답해줘" +
            "대답은 최대한 자세하게 주어진 팀 목록은 다 기억하고 있어야해 하나도 빼먹지 말고 응답이 느려도 되니까 자세하게!" +
            " 예를 들어 용인에서 활동하는 모든 팀 알려줘라고 하면 주어진 팀 목록을 하나하나 다 뒤져가면서 teamLocation에 용인이 포함된 팀 모두 알려줘야해";

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