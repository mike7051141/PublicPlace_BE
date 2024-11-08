package com.springboot.publicplace.controller;

import com.springboot.publicplace.service.KakaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Slf4j
@Controller
@RequestMapping("/api/v1/kakao")
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService kakaoService;

    @Value("${KAKAO_CLIENT_ID}")
    private String client_id;

    //    @Value("${kakao.redirect.url}")
//    private String redirect_url;

    @Autowired
    private HttpServletRequest request;


    @GetMapping("/page")
    public ResponseEntity<String> loginPage() {
        // 요청의 호스트 정보를 읽어 동적으로 redirect_url 구성
//        String host = request.getRequestURL().toString().replace(request.getRequestURI(), "");
//        String redirectUrl = host.replace(":8080", ":3000") + "/api/v1/kakao/callback";

        String serverName = request.getServerName();
        String redirectUrl;
        log.info("Server Name: {}", serverName);
        // 서버 이름에 따라 리다이렉트 URL 설정
        if (serverName.equals("3.36.221.111")) {
            redirectUrl = "http://publicplace.site/api/v1/kakao/callback";
        } else {
            // 기본 리다이렉트 URL 설정
            redirectUrl = "http://localhost:3000/api/v1/kakao/callback";
        }

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
        String serverName = request.getServerName();
        String redirectUrl;

        // 서버 이름에 따라 리다이렉트 URL 설정
        if (serverName.equals("3.36.221.111")) {
            redirectUrl = "http://publicplace.site/api/v1/kakao/callback";
        } else {
            // 기본 리다이렉트 URL 설정
            redirectUrl = "http://localhost:3000/api/v1/kakao/callback";
        }
        // 카카오 서비스에 code와 redirectUrl을 전달
        ResponseEntity<?> signInResultDto = kakaoService.getKaKaoUserInfo(code, redirectUrl);
        return ResponseEntity.status(HttpStatus.OK).body(signInResultDto);
    }
}