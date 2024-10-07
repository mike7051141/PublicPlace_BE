package com.springboot.publicplace.service.impl;

import com.springboot.publicplace.config.security.JwtTokenProvider;
import com.springboot.publicplace.dto.CommonResponse;
import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.SignDto.SignInResultDto;
import com.springboot.publicplace.dto.SignDto.SignUpDto;
import com.springboot.publicplace.dto.SignDto.SignUpResultDto;
import com.springboot.publicplace.entity.User;
import com.springboot.publicplace.exception.DuplicateResourceException;
import com.springboot.publicplace.exception.InvalidCredentialsException;
import com.springboot.publicplace.exception.ResourceNotFoundException;
import com.springboot.publicplace.repository.UserRepository;
import com.springboot.publicplace.service.SignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class SignServiceImpl implements SignService {

    private Logger logger = LoggerFactory.getLogger(SignServiceImpl.class);

    private JwtTokenProvider jwtTokenProvider;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

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

        if (user == null) {
            throw new ResourceNotFoundException("User not found with email: " + email);
        }

        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new InvalidCredentialsException("Invalid password for email: " + email);
        }
        logger.info("[getSignInResult] 패스워드 일치");

        logger.info("[getSignInResult] SignInResultDto 객체 생성");
        SignInResultDto signInResultDto = SignInResultDto.builder()
                .token(jwtTokenProvider.createToken(String.valueOf(user.getEmail()),
                        user.getRoles()))
                .success(true)
                .code(HttpStatus.OK.value())
                .msg("로그인에 성공하였습니다.")
                .build();
        logger.info("[getSignInResult] SignInResultDto 객체에 값 주입");
        return signInResultDto;
    }

    @Override
    public ResultDto checkEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("이미 존재하는 이메일입니다.");
        }
        return ResultDto.builder()
                .success(true)
                .msg("사용 가능한 이메일입니다.")
                .code(HttpStatus.OK.value())
                .build();
    }

    @Override
    public ResultDto checkPhoneNum(String phoneNum) {
        if (userRepository.existsByPhoneNumber(phoneNum)) {
            throw new DuplicateResourceException("이미 존재하는 전화번호입니다.");
        }
        return ResultDto.builder()
                .success(true)
                .msg("사용 가능한 전화번호입니다.")
                .code(HttpStatus.OK.value())
                .build();
    }

    @Override
    public ResultDto checkNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new DuplicateResourceException("이미 존재하는 닉네임입니다.");
        }
        return ResultDto.builder()
                .success(true)
                .msg("사용 가능한 닉네임입니다.")
                .code(HttpStatus.OK.value())
                .build();
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