package com.springboot.publicplace.service.impl;

import com.springboot.publicplace.config.security.JwtTokenProvider;
import com.springboot.publicplace.dto.CommonResponse;
import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.KakaoUserUpdateDto;
import com.springboot.publicplace.dto.request.LocalUserUpdateDto;
import com.springboot.publicplace.dto.response.MyPageTeamResponseDto;
import com.springboot.publicplace.dto.response.UserResponseDto;
import com.springboot.publicplace.entity.Team;
import com.springboot.publicplace.entity.TeamJoinRequest;
import com.springboot.publicplace.entity.TeamUser;
import com.springboot.publicplace.entity.User;
import com.springboot.publicplace.exception.InvalidTokenException;
import com.springboot.publicplace.exception.ResourceNotFoundException;
import com.springboot.publicplace.repository.TeamJoinRequestRepository;
import com.springboot.publicplace.repository.TeamRepository;
import com.springboot.publicplace.repository.TeamUserRepository;
import com.springboot.publicplace.repository.UserRepository;
import com.springboot.publicplace.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final TeamRepository teamRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto getUser(HttpServletRequest servletRequest) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        UserResponseDto userResponseDto = new UserResponseDto();
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
        return userResponseDto;
    }

    @Override
    public ResultDto updateKakaoUser(HttpServletRequest servletRequest, KakaoUserUpdateDto kakaoUserUpdateDto) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        if (!user.getLoginApproach().equals("Kakao-Login")) {
            throw new RuntimeException("카카오 유저 정보 업데이트를 실패하였습니다.");
        }

        User kakaoUser = userRepository.findByEmail(email);
        kakaoUser.setNickname(kakaoUserUpdateDto.getNickname());
        kakaoUser.setGender(kakaoUserUpdateDto.getGender());
        kakaoUser.setPhoneNumber(kakaoUserUpdateDto.getPhoneNumber());
        kakaoUser.setFoot(kakaoUserUpdateDto.getFoot());
        kakaoUser.setProfileImg(kakaoUserUpdateDto.getProfileImg());
        kakaoUser.setPosition(kakaoUserUpdateDto.getPosition());
        kakaoUser.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        ResultDto resultDto = ResultDto.builder()
                .success(true)
                .msg("카카오 유저 정보 업데이트를 완료하였습니다.")
                .code(HttpStatus.OK.value())
                .build();
        return resultDto;
    }

    @Override
    public ResultDto updateLocalUser(HttpServletRequest servletRequest, LocalUserUpdateDto localUserUpdateDto) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        if (!user.getLoginApproach().equals("Local-Login")) {
            throw new RuntimeException("로컬 유저 정보 업데이트를 실패하였습니다.");
        }

        User localUser = userRepository.findByEmail(email);
        localUser.setNickname(localUserUpdateDto.getNickname());
        localUser.setGender(localUserUpdateDto.getGender());
        localUser.setPassword(passwordEncoder.encode(localUserUpdateDto.getPassword()));
        localUser.setPhoneNumber(localUserUpdateDto.getPhoneNumber());
        localUser.setFoot(localUserUpdateDto.getFoot());
        localUser.setProfileImg(localUserUpdateDto.getProfileImg());
        localUser.setPosition(localUserUpdateDto.getPosition());
        localUser.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        ResultDto resultDto = ResultDto.builder()
                .success(true)
                .msg("로컬 유저 정보 업데이트를 완료하였습니다.")
                .code(HttpStatus.OK.value())
                .build();
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

    @Override
    public ResultDto deleteTeamJoinRequest(HttpServletRequest servletRequest, Long teamId) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("팀을 찾을 수 없습니다."));

        TeamJoinRequest teamJoinRequest = teamJoinRequestRepository.findByTeamAndUser(team, user);

        if (teamJoinRequest == null) {
            throw new ResourceNotFoundException("신청서를 찾을 수 없습니다.");
        }

        teamJoinRequestRepository.delete(teamJoinRequest);

        ResultDto resultDto = ResultDto.builder()
                .success(true)
                .msg("팀 신청이 취소 되었습니다.")
                .code(HttpStatus.OK.value())
                .build();
        return resultDto;
    }
}
