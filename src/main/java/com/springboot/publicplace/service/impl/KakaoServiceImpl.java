package com.springboot.publicplace.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.publicplace.config.security.JwtTokenProvider;
import com.springboot.publicplace.dto.SignDto.JwtResponse;
import com.springboot.publicplace.dto.SignDto.KakaoResponseDto;
import com.springboot.publicplace.entity.User;
import com.springboot.publicplace.repository.UserRepository;
import com.springboot.publicplace.service.KakaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoServiceImpl implements KakaoService {
    @Value("${KAKAO_CLIENT_ID}")
    private String clientKey;
    @Value("${kakao.redirect.url}")
    private String redirectUrl;
    @Value("${kakao.accesstoken.url}")
    private String kakaoAccessTokenUrl;
    @Value("${kakao.userinfo.url}")
    private String kakaoUserInfoUrl;

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public ResponseEntity<?> getKaKaoUserInfo(String authorizeCode, String redirectUrl) {
        log.info("[kakao login] issue a authorizecode");
        ObjectMapper objectMapper = new ObjectMapper(); // json 파싱해주는 객체
        RestTemplate restTemplate = new RestTemplate(); // client 연결 객체

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientKey);
        params.add("redirect_uri", redirectUrl);
        params.add("code", authorizeCode);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        try{
            ResponseEntity<String> response = restTemplate.exchange(
                    kakaoAccessTokenUrl,
                    HttpMethod.POST,
                    kakaoTokenRequest,
                    String.class
            );
            log.info("[kakao login] authorizecode issued successfully");
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
            String accessToken = (String) responseMap.get("access_token");

            KakaoResponseDto kakaoUserInfo = getInfo(accessToken);
            if (kakaoUserInfo == null) {
                return ResponseEntity.status(401).body("Invalid Kakao login");
            }
            // 사용자 정보가 있다면 이메일을 기준으로 DB에서 사용자 찾기
            User user = userRepository.findByEmail(kakaoUserInfo.getEmail());

            String refreshToken;
            if (user == null) {
                // 새로운 사용자는 DB에 저장
                refreshToken = jwtTokenProvider.createRefreshToken(kakaoUserInfo.getEmail());
                user = User.builder()
                        .email(kakaoUserInfo.getEmail())
                        .name(kakaoUserInfo.getName())
                        .phoneNumber(kakaoUserInfo.getPhoneNumber())
                        .profileImg(kakaoUserInfo.getProfileUrl())
                        .nickname(kakaoUserInfo.getNickname())
                        .gender(kakaoUserInfo.getGender())
                        .ageRange(kakaoUserInfo.getAgeRange())
                        .loginApproach("Kakao-Login")
                        .password("1") // 카카오 로그인에서는 비밀번호가 필요하지 않음
                        .roles(Collections.singletonList("ROLE_ADMIN")) // 기본 역할 설정
                        .refreshToken(refreshToken)
                        .build();
                userRepository.save(user);
            } else {
                refreshToken = jwtTokenProvider.refreshIfExpired(user.getEmail(), user.getRefreshToken());
            }
            // 새로운 엑세스 토큰 발급
            String token = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRoles());
            return ResponseEntity.ok(new JwtResponse(true, 0, "Success", token, refreshToken));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private KakaoResponseDto getInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ObjectMapper mapper = new ObjectMapper();

        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(kakaoUserInfoUrl, entity, String.class);

        try {
            Map<String, Object> responseMap = mapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
            Map<String, Object> kakaoAccount = (Map<String, Object>) responseMap.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            // Gender 변환 로직 추가
            String gender = (String) kakaoAccount.get("gender");
            String displayGender;
            if ("female".equals(gender)) {
                displayGender = "여자";
            } else if ("male".equals(gender)) {
                displayGender = "남자";
            } else {
                displayGender = "기타"; // 기본값 설정
            }

            KakaoResponseDto requestSignUpDto = KakaoResponseDto.builder()
                    .name((String) kakaoAccount.get("name"))
                    .phoneNumber((String) kakaoAccount.get("phone_number"))
                    .email((String) kakaoAccount.get("email"))
                    .profileUrl((String) profile.get("profile_image_url"))
                    .gender(displayGender) // 카카오 로그인에선 male로 오기 때문에 남자로 변환함
                    .ageRange((String) kakaoAccount.get("age_range"))
                    .nickname((String) profile.get("nickname"))
                    .build();
            // User 엔티티를 생성하고 데이터베이스에 저장
            User user = userRepository.findByEmail(requestSignUpDto.getEmail());
            if (user == null) {
                user = User.builder()
                        .email(requestSignUpDto.getEmail())
                        .name(requestSignUpDto.getName())
                        .phoneNumber(requestSignUpDto.getPhoneNumber())
                        .profileImg(requestSignUpDto.getProfileUrl())
                        .nickname(requestSignUpDto.getNickname())
                        .gender(requestSignUpDto.getGender())
                        .ageRange(requestSignUpDto.getAgeRange())
                        .loginApproach("Kakao-Login")
                        .password("1") // 카카오 로그인에서는 비밀번호가 필요하지 않음
                        .roles(Collections.singletonList("ROLE_ADMIN")) // 기본 역할 설정
                        .build();
                userRepository.save(user);
            }
            return requestSignUpDto;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}