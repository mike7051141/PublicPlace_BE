package com.springboot.publicplace.controller;

import com.springboot.publicplace.entity.Message;
import com.springboot.publicplace.service.GPTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class GPTController {

    private final GPTService gptService;

    @PostMapping("/GPT")
    public ResponseEntity<List<Message>> chat(@RequestBody Map<String, String> request, @ApiIgnore HttpSession session) {
        String prompt = request.get("prompt");
        if (prompt == null || prompt.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        List<Message> responseMessages = gptService.getChatResponses(prompt, session);
        return ResponseEntity.ok(responseMessages);
    }
}
