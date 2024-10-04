package com.springboot.publicplace.service.impl;

import com.springboot.publicplace.config.security.JwtTokenProvider;
import com.springboot.publicplace.dto.CommonResponse;
import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.response.TeamJoinDetailResponseDto;
import com.springboot.publicplace.dto.response.TeamJoinResponseDto;
import com.springboot.publicplace.entity.Team;
import com.springboot.publicplace.entity.TeamJoinRequest;
import com.springboot.publicplace.entity.TeamUser;
import com.springboot.publicplace.entity.User;
import com.springboot.publicplace.exception.ResourceNotFoundException;
import com.springboot.publicplace.exception.UnauthorizedActionException;
import com.springboot.publicplace.exception.UserNotInTeamException;
import com.springboot.publicplace.repository.TeamJoinRequestRepository;
import com.springboot.publicplace.repository.TeamRepository;
import com.springboot.publicplace.repository.TeamUserRepository;
import com.springboot.publicplace.repository.UserRepository;
import com.springboot.publicplace.service.TeamJoinAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamJoinAdminServiceImpl implements TeamJoinAdminService {
    private final JwtTokenProvider jwtTokenProvider;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamUserRepository teamUserRepository;
    private final TeamJoinRequestRepository teamJoinRequestRepository;
    @Override
    public List<TeamJoinResponseDto> getJoinRequestList(Long teamId, HttpServletRequest servletRequest) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        // 해당 팀 조회
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("팀을 찾을 수 없습니다."));

        // 회장 여부 확인
        if (!isTeamLeader(team, user)) {
            throw new UnauthorizedActionException("회장만 이 작업을 수행할 수 있습니다.");
        }

        List<TeamJoinRequest> requests = teamJoinRequestRepository.findAllByTeamAndStatus(team, "PENDING");

        return requests.stream()
                .map(request -> new TeamJoinResponseDto(
                        request.getRequestId(),
                        request.getUser().getName(),
                        request.getUser().getPhoneNumber()
                ))
                .collect(Collectors.toList());
    }

    public TeamJoinDetailResponseDto getJoinRequestDetail(Long requestId, HttpServletRequest servletRequest){
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        TeamJoinRequest request = teamJoinRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("신청서를 찾을 수 없습니다."));

        Team team = request.getTeam();

        // 회장 여부 확인
        if (!isTeamLeader(team, user)) {
            throw new UnauthorizedActionException("회장만 이 작업을 수행할 수 있습니다.");
        }
        // 상세 정보 DTO로 반환
        TeamJoinDetailResponseDto detailDto = new TeamJoinDetailResponseDto(
                request.getRequestId(),
                request.getUserName(),
                request.getUserGender(),
                request.getUserAgeRange(),
                request.getUserPhoneNumber(),
                request.getJoinReason(),
                request.getStatus(),
                request.getRole(),
                request.getRequestDate()
        );
        return detailDto;
    }

    @Override
    public ResultDto approveJoinRequest(Long requestId, HttpServletRequest servletRequest) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        TeamJoinRequest request = teamJoinRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("신청서를 찾을 수 없습니다."));

        Team team = request.getTeam();
        // 회장 여부 확인
        if (!isTeamLeader(team, user)) {
            throw new UnauthorizedActionException("회장만 이 작업을 수행할 수 있습니다.");
        }

        // 팀원으로 추가
        TeamUser teamUser = new TeamUser();
        teamUser.setTeam(request.getTeam());
        teamUser.setUser(request.getUser());
        teamUser.setRole(request.getRole());

        // 팀원 저장
        teamUserRepository.save(teamUser);

        // 가입 요청 삭제
        teamJoinRequestRepository.delete(request);

        ResultDto resultDto = ResultDto.builder()
                .success(true)
                .msg("팀 가입 요청을 승인하였습니다.")
                .code(HttpStatus.OK.value())
                .build();
        return resultDto;
    }

    @Override
    public ResultDto rejectJoinRequest(Long requestId, HttpServletRequest servletRequest) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        TeamJoinRequest request = teamJoinRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("신청서를 찾을 수 없습니다."));

        Team team = request.getTeam();
        // 회장 여부 확인
        if (!isTeamLeader(team, user)) {
            throw new UnauthorizedActionException("회장만 이 작업을 수행할 수 있습니다.");
        }

        // 가입 요청 삭제
        teamJoinRequestRepository.delete(request);

        ResultDto resultDto = ResultDto.builder()
                .success(true)
                .msg("팀 가입 요청을 거절하였습니다.")
                .code(HttpStatus.OK.value())
                .build();
        return resultDto;
    }

    public boolean isTeamLeader(Team team, User user) {
        TeamUser teamUser = teamUserRepository.findByTeamAndUser(team,user)
                .orElseThrow(() -> new UserNotInTeamException("팀에 가입된 사용자가 아닙니다."));
        return teamUser != null && "회장".equals(teamUser.getRole());
    }
}
