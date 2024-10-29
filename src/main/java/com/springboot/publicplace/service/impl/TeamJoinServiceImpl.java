package com.springboot.publicplace.service.impl;

import com.springboot.publicplace.config.security.JwtTokenProvider;
import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.entity.*;
import com.springboot.publicplace.exception.DuplicateResourceException;
import com.springboot.publicplace.exception.ResourceNotFoundException;
import com.springboot.publicplace.exception.UserNotInTeamException;
import com.springboot.publicplace.repository.TeamJoinRequestRepository;
import com.springboot.publicplace.repository.TeamRepository;
import com.springboot.publicplace.repository.TeamUserRepository;
import com.springboot.publicplace.repository.UserRepository;
import com.springboot.publicplace.service.TeamJoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class TeamJoinServiceImpl implements TeamJoinService {
    private final JwtTokenProvider jwtTokenProvider;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamUserRepository teamUserRepository;
    private final TeamJoinRequestRepository teamJoinRequestRepository;

    @Override
    public ResultDto joinTeam(Long teamId, HttpServletRequest servletRequest, String joinReason) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        // 팀 조회
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("팀을 찾을 수 없습니다."));

        // 이미 팀에 가입된 상태인지 확인
        boolean alreadyJoined = teamUserRepository.existsByTeamAndUser(team, user);
        if (alreadyJoined) {
            throw new DuplicateResourceException("이미 팀에 가입된 유저입니다.");
        }

        // 이미 가입 신청서를 제출했는지 확인
        boolean alreadyRequested = teamJoinRequestRepository.existsByTeamAndUserAndStatus(team, user, Status.PENDING);
        if (alreadyRequested) {
            throw new DuplicateResourceException("이미 팀에 가입 신청을 한 상태입니다.");
        }

        // 팀 가입 신청서 생성
        TeamJoinRequest joinRequest = new TeamJoinRequest();
        joinRequest.setTeam(team);
        joinRequest.setUser(user);
        joinRequest.setRole(RoleType.팀원);
        joinRequest.setStatus(Status.PENDING);
//        joinRequest.setUserName(user.getName());
//        joinRequest.setUserGender(user.getGender());
//        joinRequest.setUserAgeRange(user.getAgeRange());
//        joinRequest.setUserPhoneNumber(user.getPhoneNumber());
//        joinRequest.setUserProfileImg(user.getProfileImg());
//        joinRequest.setFoot(user.getFoot());
//        joinRequest.setPosition(user.getPosition());
        joinRequest.setJoinReason(joinReason);

        teamJoinRequestRepository.save(joinRequest);

        ResultDto resultDto = ResultDto.builder()
                .success(true)
                .msg("팀 가입 신청을 완료하였습니다")
                .code(HttpStatus.OK.value())
                .build();
        return resultDto;
    }

    @Override
    public ResultDto leaveTeam(Long teamId, HttpServletRequest servletRequest){
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("팀을 찾을 수 없습니다."));

        TeamUser teamUser = teamUserRepository.findByTeamAndUser(team, user)
                 .orElseThrow(() -> new UserNotInTeamException("팀에 가입된 사용자가 아닙니다."));

        teamUserRepository.delete(teamUser);

        ResultDto resultDto = ResultDto.builder()
                .success(true)
                .msg("팀을 탈퇴하였습니다.")
                .code(HttpStatus.OK.value())
                .build();
        return resultDto;
    }
}
