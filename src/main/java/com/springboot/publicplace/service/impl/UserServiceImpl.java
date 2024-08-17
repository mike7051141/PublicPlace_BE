package com.springboot.publicplace.service.impl;

import com.springboot.publicplace.config.security.JwtTokenProvider;
import com.springboot.publicplace.dto.CommonResponse;
import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.SignDto.SignUpResultDto;
import com.springboot.publicplace.dto.request.UserUpdateDto;
import com.springboot.publicplace.dto.response.UserResponseDto;
import com.springboot.publicplace.entity.User;
import com.springboot.publicplace.repository.UserRepository;
import com.springboot.publicplace.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public UserResponseDto getUser(HttpServletRequest servletRequest) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);

        UserResponseDto userResponseDto = new UserResponseDto();
        if (jwtTokenProvider.validationToken(token)) {
            User user = userRepository.findByEmail(email);
            userResponseDto.setUserId(user.getUserId());
            userResponseDto.setName(user.getName());
            userResponseDto.setEmail(user.getEmail());
            userResponseDto.setAgeRange(user.getAgeRange());
            userResponseDto.setNickname(user.getNickname());
            userResponseDto.setProfileImg(user.getProfileImg());
            userResponseDto.setPhoneNumber(user.getPhoneNumber());
            userResponseDto.setPosition(user.getPosition());
            userResponseDto.setFoot(user.getFoot());
            userResponseDto.setLoginApproach(user.getLoginApproach());
            userResponseDto.setGender(user.getGender());
            userResponseDto.setCreatedAt(String.valueOf(user.getCreatedAt()));
            userResponseDto.setUpdatedAt(String.valueOf(user.getUpdatedAt()));
        }
        return userResponseDto;
    }

    @Override
    public ResultDto updateUser(HttpServletRequest servletRequest, UserUpdateDto userUpdateDto) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);

        ResultDto resultDto = new ResultDto();
        if (jwtTokenProvider.validationToken(token)) {
            User user = userRepository.findByEmail(email);
            user.setNickname(userUpdateDto.getNickname());
            user.setGender(userUpdateDto.getGender());
            user.setPhoneNumber(userUpdateDto.getPhoneNumber());
            user.setFoot(userUpdateDto.getFoot());
            user.setProfileImg(userUpdateDto.getProfileImg());
            user.setPosition(userUpdateDto.getPosition());
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            setSuccess(resultDto);
        }else {
            setFail(resultDto);
        }
        return resultDto;
    }
    private void setSuccess(ResultDto resultDto){
        resultDto.setSuccess(true);
        resultDto.setCode(CommonResponse.SUCCESS.getCode());

        resultDto.setMsg(CommonResponse.SUCCESS.getMsg());
    }

    private void setFail(ResultDto resultDto){
        resultDto.setSuccess(true);
        resultDto.setCode(CommonResponse.Fail.getCode());

        resultDto.setMsg(CommonResponse.Fail.getMsg());
    }
}
