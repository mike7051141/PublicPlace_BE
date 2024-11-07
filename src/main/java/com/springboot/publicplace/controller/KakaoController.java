package com.springboot.publicplace.controller;

import com.springboot.publicplace.dto.SignDto.SignInResultDto;
import com.springboot.publicplace.service.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api/v1/kakao")
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService kakaoService;

    @Value("${kakao.client.id}")
    private String client_id;

//    @Value("${kakao.redirect.url}")
//    private String redirect_url;

    @Autowired
    private HttpServletRequest request;

    @GetMapping("/page")
    public ResponseEntity<String> loginPage() {
        // 요청의 호스트 정보를 읽어 동적으로 redirect_url 구성
        String host = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        String redirectUrl = host + "/api/v1/kakao/callback";

        // 카카오 인증 URL 생성
        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + client_id + "&redirect_uri=" + redirectUrl;

        return ResponseEntity.status(HttpStatus.OK).body(location);
    }


    @ApiIgnore
    @GetMapping("/callback")
    @ResponseBody
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        return ResponseEntity.status(HttpStatus.OK).body(code);
    }

    @GetMapping("/login")
    @ResponseBody
    public ResponseEntity<?> kakaoLogin(@RequestParam String code, HttpServletRequest servletRequest) {
        // 요청의 호스트 정보를 읽어 동적으로 redirect_url 구성
        String host = servletRequest.getRequestURL().toString().replace(servletRequest.getRequestURI(), "");
        String redirectUrl = host + "/api/v1/kakao/callback";

        // 카카오 서비스에 code와 redirectUrl을 전달
        ResponseEntity<?> signInResultDto = kakaoService.getKaKaoUserInfo(code, redirectUrl);
        return ResponseEntity.status(HttpStatus.OK).body(signInResultDto);
    }
}