package com.springboot.publicplace.service.impl;

import com.springboot.publicplace.controller.GPTController;
import com.springboot.publicplace.dto.request.GPTRequest;
import com.springboot.publicplace.dto.response.GPTResponse;
import com.springboot.publicplace.entity.Message;
import com.springboot.publicplace.service.GPTService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    // 시스템 프롬프트 정의 (프롬프트 엔지니어링)
    private static final String SYSTEM_PROMPT = "너는 역사 선생님이야 아이들에게 친절하게 답해줘";

    @Override
    public List<Message> getChatResponses(String prompt, HttpSession session) {
        // 세션에서 이전 대화 내용 가져오기
        List<Message> messages = (List<Message>) session.getAttribute("messages");
        if (messages == null) {
            messages = new ArrayList<>();
            // 시스템 프롬프트 추가
            messages.add(new Message("system", SYSTEM_PROMPT));
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