package com.springboot.publicplace.config.security;

import com.springboot.publicplace.entity.User;
import com.springboot.publicplace.exception.UserNotInTeamException;
import com.springboot.publicplace.repository.UserRepository;
import com.springboot.publicplace.service.UserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;


    @Value("${JWT_SECRET}")
    private String secretKey = "secretKey";

    @Value("${REFRESH_SECRET}")
    private String refreshKey = "refreshKey";

    // AccessToken 유효 기간 (1시간)
    private final Long accessTokenValidTime = 1000L * 60 * 60;

    // RefreshToken 유효 기간(2주)
    private final Long refreshTokenValidTime = 1000L * 60 * 60 * 24 * 14;

    @PostConstruct
    protected void init(){
        logger.info("[init] 시크릿 초기화 시작");
        System.out.println("초기화 전 : "+secretKey);

        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        refreshKey = Base64.getEncoder().encodeToString(refreshKey.getBytes());
    }

    // AccessToken 생성 메서드
    public String createAccessToken(String email, List<String> roles){
        return createToken(email, roles, accessTokenValidTime, secretKey);
    }

    // RefreshToken 생성 메서드
    public String createRefreshToken(String email) {
        String refreshToken = createToken(email, null, refreshTokenValidTime, refreshKey);
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setRefreshToken(refreshToken);
        }else {
            throw new UserNotInTeamException("User not found with email: " + email);
        }
        return refreshToken;
    }

    // jwt 토큰 생성
    public String createToken(String email, List<String> roles, Long tokenValidTime, String key) {
        Claims claims = Jwts.claims().setSubject(email);
        if (roles != null) {
            claims.put("roles", roles);
        }

        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date((now.getTime() + tokenValidTime)))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();

        logger.info("[createToken] 토큰 생성 완료", token);
        return token;
    }

    // jwt 토큰에서 회원 구별 조회
    public String getUsername(String token){
        logger.info("[getUsername] 회원 구별 조회 시작");
        String info = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody() // 토큰 본문
                .getSubject(); // 토큰에서 Subject -> 회원 구별
        logger.info("[getUsername] 토큰 기반 회원 구별 정보 추출 완료, info : {}", info);
        return info;
    }

    //jwt 토큰으로 인증 정보 조회
    public Authentication getAuthentication(String token){
        logger.info("[getAuthentication] 토큰 인증 정보 조회 시작");

        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsername(token));
        logger.info("[getAuthenticaiton] 토큰 인증 정보 조회 완료, UserDetails email",userDetails.getUsername());

        return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    }
    //HTTP 헤더 정보에 X-AUTH-TOKEN 추가
    public String resolveToken(HttpServletRequest request){
        return request.getHeader("X-AUTH-TOKEN");
    }

    public boolean validationToken(String token) {
        return validateTokenWithKey(token, secretKey);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return validateTokenWithKey(refreshToken, refreshKey);
    }

    public boolean validateTokenWithKey(String token, String key){
        try{
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        }catch (Exception e){
            return false;
        }
    }
    public String refreshIfExpired(String email, String refreshToken) {
        // DB에 저장된 리프레시 토큰이 null 인지 확인
        if (refreshToken == null || !validateRefreshToken(refreshToken)) {
            // 리프레시 토큰이 없거나 만료된 경우 새 리프레시 토큰 생성
            String newRefreshToken = createRefreshToken(email);
            User user = userRepository.findByEmail(email);
            if (user != null) {
                user.setRefreshToken(newRefreshToken); // 새 토큰을 DB에 저장
                userRepository.save(user); // DB 업데이트
            }
            return newRefreshToken; // 새로 발급한 리프레시 토큰 반환
        }

        // 리프레시 토큰이 유효한 경우 기존 리프레시 토큰 반환
        return refreshToken;
    }

    public String getUsernameFromRefreshToken(String refreshToken) {
        try {
            // Refresh Token에서 사용자 이메일 추출 (JWT의 subject 필드 사용)
            return Jwts.parser()
                    .setSigningKey(refreshKey) // Refresh Token 암호화 키 사용
                    .parseClaimsJws(refreshToken)
                    .getBody()
                    .getSubject(); // subject 필드에서 사용자 이메일(ID) 추출
        } catch (Exception e) {
            // Refresh Token이 유효하지 않을 경우 null 반환
            throw new IllegalArgumentException("Invalid Refresh Token", e);
        }
    }
}
