package com.springboot.publicplace.service;

import org.springframework.http.ResponseEntity;

public interface KakaoService {
    ResponseEntity<?> getKaKaoUserInfo(String code);
}
