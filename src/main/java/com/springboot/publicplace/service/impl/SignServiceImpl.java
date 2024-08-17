package com.springboot.publicplace.service.impl;

import com.springboot.publicplace.config.security.JwtTokenProvider;
import com.springboot.publicplace.dto.CommonResponse;
import com.springboot.publicplace.dto.SignDto.SignInResultDto;
import com.springboot.publicplace.dto.SignDto.SignUpDto;
import com.springboot.publicplace.dto.SignDto.SignUpResultDto;
import com.springboot.publicplace.entity.User;
import com.springboot.publicplace.repository.UserRepository;
import com.springboot.publicplace.service.S3UploadService;
import com.springboot.publicplace.service.SignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class SignServiceImpl implements SignService {

    private Logger logger = LoggerFactory.getLogger(SignServiceImpl.class);

    private JwtTokenProvider jwtTokenProvider;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private S3UploadService s3Service;

    @Autowired
    public SignServiceImpl(UserRepository userRepository , JwtTokenProvider jwtTokenProvider,
                           PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;

    }

    @Override
    public SignUpResultDto SignUp(SignUpDto signUpDto, String roles) {
        User user;
        if(roles.equalsIgnoreCase("admin")){
            user = User.builder()
                    .password(passwordEncoder.encode(signUpDto.getPassword()))
                    .name(signUpDto.getName())
                    .email(signUpDto.getEmail())
                    .phoneNumber(signUpDto.getPhoneNumber())
                    .nickname(signUpDto.getNickname())
                    .profileImg(signUpDto.getProfileImg())
                    .gender(signUpDto.getGender())
                    .ageRange(signUpDto.getAgeRange())
                    .foot(signUpDto.getFoot())
                    .position(signUpDto.getPosition())
                    .loginApproach("Local-Login")
                    .roles(Collections.singletonList("ROLE_ADMIN"))
                    .build();
        }else{
            user = User.builder()
                    .password(passwordEncoder.encode(signUpDto.getPassword()))
                    .name(signUpDto.getName())
                    .email(signUpDto.getEmail())
                    .phoneNumber(signUpDto.getPhoneNumber())
                    .nickname(signUpDto.getNickname())
                    .profileImg(signUpDto.getProfileImg())
                    .gender(signUpDto.getGender())
                    .ageRange(signUpDto.getAgeRange())
                    .roles(Collections.singletonList("ROLE_USER"))
                    .build();
        }
        User saveUser = userRepository.save(user);

        SignUpResultDto signUpResultDto = new SignUpResultDto();
        logger.info("[getSignResultDto] USER 정보 들어왔는지 확인 후 결과값 주입");

        if(!saveUser.getEmail().isEmpty()){
            setSuccess(signUpResultDto);
        }else{
            setFail(signUpResultDto);
        }

        return signUpResultDto;
    }

    @Override
    public SignInResultDto SignIn(String email, String password) throws RuntimeException {
        logger.info("[getSignInResult] signDataHandler로 회원정보 요청");
        User user = userRepository.findByEmail(email);
        logger.info("[getSignInResult] EMAIL:{}", email);

        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException();
        }
        logger.info("[getSignInResult] 패스워드 일치");

        logger.info("[getSignInResult] SignInResultDto 객체 생성");
        SignInResultDto signInResultDto = SignInResultDto.builder()
                .token(jwtTokenProvider.createToken(String.valueOf(user.getEmail()),
                        user.getRoles()))
                .build();
        logger.info("[getSignInResult] SignInResultDto 객체에 값 주입");
        setSuccess(signInResultDto);
        return signInResultDto;
    }

    private void setSuccess(SignUpResultDto result){
        result.setSuccess(true);
        result.setCode(CommonResponse.SUCCESS.getCode());
        result.setMsg(CommonResponse.SUCCESS.getMsg());
    }

    private void setFail(SignUpResultDto result){
        result.setSuccess(false);
        result.setCode(CommonResponse.Fail.getCode());
        result.setMsg(CommonResponse.Fail.getMsg());

    }
}