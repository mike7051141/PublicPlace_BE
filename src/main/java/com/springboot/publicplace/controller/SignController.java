package com.springboot.publicplace.controller;

import com.springboot.publicplace.dto.SignDto.SignInResultDto;
import com.springboot.publicplace.dto.SignDto.SignUpDto;
import com.springboot.publicplace.dto.SignDto.SignUpResultDto;
import com.springboot.publicplace.service.SignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/sign-api")
public class SignController {

    private Logger logger = LoggerFactory.getLogger(SignController.class);
    private SignService signService;

    @Autowired
    public SignController(SignService signService) {

        this.signService = signService;
    }

    @PostMapping("/sign-up")
    public SignUpResultDto SignUp(@RequestBody SignUpDto signUpDto, String roles) {
        logger.info("[signUp] 회원가입을 수행합니다. email : {}, password : ****, name : {}, role : {}", signUpDto.getEmail(),
                signUpDto.getPassword(), roles);
        SignUpResultDto signUpResultDto = signService.SignUp(signUpDto, roles);
        return signUpResultDto;
    }

    @PostMapping("/sign-in")
    public SignInResultDto SignIn(@RequestParam String email, String password) {
        logger.info("[sign-in] 로그인을 시도하고 있습니다. id : {}, password : *****", email);
        SignInResultDto signInResultDto = signService.SignIn(email, password);
        if (signInResultDto.getCode() == 0) {
            logger.info("[sign-in] 정상적으로 로그인이 되었습니다. id: {}, token : {}", email, signInResultDto.getToken());
            signInResultDto.getToken();
        }
        return signInResultDto;
    }

    @GetMapping(value = "/exception")
    public void exceptionTest() throws RuntimeException {
        throw new RuntimeException("접근이 금지 되었습니다.");
    }

//    @ExceptionHandler(value = RuntimeException.class)
//    public ResponseEntity<Map<String, String>> ExceptionHandler(RuntimeException e) {
//        HttpHeaders responseHeaders = new HttpHeaders();
//        //responseHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
//        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
//
//        logger.error("ExceptionHandler 호출, {}, {}", e.getCause(), e.getMessage());
//
//        Map<String, String> map = new HashMap<>();
//        map.put("error type", httpStatus.getReasonPhrase());
//        map.put("code", "400");
//        map.put("message", "에러 발생");
//
//        return new ResponseEntity<>(map, responseHeaders, httpStatus);
//    }

}
