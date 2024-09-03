package com.springboot.publicplace.controller;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.service.SignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/signCheck")
@RequiredArgsConstructor
public class SignCheckController {

    private final SignService signService;

    @GetMapping("/checkEmail/{email}")
    public ResponseEntity<ResultDto> checkEmail(@PathVariable String email){
        ResultDto resultDto = signService.checkEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(resultDto);
    }
    @GetMapping("/checkPhoneNum/{phoneNum}")
    public ResponseEntity<ResultDto> checkPhoneNum(@PathVariable String phoneNum){
        ResultDto resultDto = signService.checkPhoneNum(phoneNum);
        return ResponseEntity.status(HttpStatus.OK).body(resultDto);
    }
    @GetMapping("/checkNickname/{nickname}")
    public ResponseEntity<ResultDto> checkNickname(@PathVariable String nickname){
        ResultDto resultDto = signService.checkNickname(nickname);
        return ResponseEntity.status(HttpStatus.OK).body(resultDto);
    }
}
