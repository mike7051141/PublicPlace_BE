package com.springboot.publicplace.controller;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.entity.Message;
import com.springboot.publicplace.service.GPTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class GPTController {

    private final GPTService gptService;

    @PostMapping("/GPT")
    public ResponseEntity<List<Message>> chat(HttpServletRequest servletRequest,
                                              @RequestParam String prompt,
                                              @ApiIgnore HttpSession session) {
        List<Message> responseMessages = gptService.getChatResponses(servletRequest, prompt, session);
        return ResponseEntity.ok(responseMessages);
    }
    @PostMapping("/refresh")
    public ResponseEntity<ResultDto> refresh(@ApiIgnore HttpSession session) {
        // 세션에서 대화 내용 삭제
        session.invalidate(); // 세션을 무효화하여 모든 속성 제거
        ResultDto resultDto = ResultDto.builder()
                .code(HttpStatus.OK.value())
                .msg("새로고침 되었습니다.")
                .success(true)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(resultDto);
    }
}
