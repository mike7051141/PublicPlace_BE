package com.springboot.publicplace.service.impl;

import com.springboot.publicplace.config.security.JwtTokenProvider;
import com.springboot.publicplace.dto.CommonResponse;
import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.SignDto.SignUpResultDto;
import com.springboot.publicplace.dto.request.UserUpdateDto;
import com.springboot.publicplace.dto.response.MyPageTeamResponseDto;
import com.springboot.publicplace.dto.response.UserResponseDto;
import com.springboot.publicplace.entity.TeamJoinRequest;
import com.springboot.publicplace.entity.TeamUser;
import com.springboot.publicplace.entity.User;
import com.springboot.publicplace.repository.TeamJoinRequestRepository;
import com.springboot.publicplace.repository.TeamUserRepository;
import com.springboot.publicplace.repository.UserRepository;
import com.springboot.publicplace.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TeamUserRepository teamUserRepository;
    private final TeamJoinRequestRepository teamJoinRequestRepository;

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

    @Override
    public List<MyPageTeamResponseDto> getJoinedTeams(HttpServletRequest servletRequest) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        List<TeamUser> teamUsers = teamUserRepository.findByUser(user);
        return teamUsers.stream()
                .map(teamUser -> MyPageTeamResponseDto.builder()
                        .teamId(teamUser.getTeam().getTeamId())         // 팀 ID
                        .teamName(teamUser.getTeam().getTeamName())     // 팀명
                        .teamImg(teamUser.getTeam().getTeamImg())       // 팀 이미지
                        .teamMembers(teamUser.getTeam().getTeamMembers()) // 팀원 수
                        .teamLocation(teamUser.getTeam().getTeamLocation()) // 팀 경기장 위치
                        .teamCreationDate(teamUser.getTeam().getCreatedAt().toString()) // 창단일
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<MyPageTeamResponseDto> getAppliedTeams(HttpServletRequest servletRequest) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        List<TeamJoinRequest> teamJoinRequests = teamJoinRequestRepository.findByUser(user);
        return teamJoinRequests.stream()
                .map(teamJoinRequest -> MyPageTeamResponseDto.builder()
                        .teamId(teamJoinRequest.getTeam().getTeamId())         // 팀 ID
                        .teamName(teamJoinRequest.getTeam().getTeamName())     // 팀명
                        .teamImg(teamJoinRequest.getTeam().getTeamImg())       // 팀 이미지
                        .teamMembers(teamJoinRequest.getTeam().getTeamMembers()) // 팀원 수
                        .teamLocation(teamJoinRequest.getTeam().getTeamLocation()) // 팀 경기장 위치
                        .teamCreationDate(teamJoinRequest.getTeam().getCreatedAt().toString()) // 창단일
                        .build())
                .collect(Collectors.toList());
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
