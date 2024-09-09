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
import com.springboot.publicplace.repository.TeamJoinRequestRepository;
import com.springboot.publicplace.repository.TeamRepository;
import com.springboot.publicplace.repository.TeamUserRepository;
import com.springboot.publicplace.repository.UserRepository;
import com.springboot.publicplace.service.TeamJoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        ResultDto resultDto = new ResultDto();
        try {
            String token = jwtTokenProvider.resolveToken(servletRequest);
            String email = jwtTokenProvider.getUsername(token);
            User user = userRepository.findByEmail(email);


            // 팀 조회
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));

            // 이미 팀에 가입된 상태인지 확인
            boolean alreadyJoined = teamUserRepository.existsByTeamAndUser(team, user);
            if (alreadyJoined) {
                throw new RuntimeException("이미 팀에 가입된 유저입니다.");
            }

            // 이미 가입 신청서를 제출했는지 확인
            boolean alreadyRequested = teamJoinRequestRepository.existsByTeamAndUserAndStatus(team, user, "PENDING");
            if (alreadyRequested) {
                throw new RuntimeException("이미 팀에 가입 신청을 한 상태입니다.");
            }

            // 팀 가입 신청서 생성
            TeamJoinRequest joinRequest = new TeamJoinRequest();
            joinRequest.setTeam(team);
            joinRequest.setUser(user);
            joinRequest.setRole("팀원");
            joinRequest.setStatus("PENDING");
            joinRequest.setRequestDate(LocalDateTime.now());

            // 새로 추가된 필드에 값 설정
            joinRequest.setUserName(user.getName());
            joinRequest.setUserGender(user.getGender());
            joinRequest.setUserAgeRange(user.getAgeRange());
            joinRequest.setUserPhoneNumber(user.getPhoneNumber());
            joinRequest.setJoinReason(joinReason);

            teamJoinRequestRepository.save(joinRequest);

            resultDto.setSuccess(true);
            resultDto.setMsg("팀 가입 신청을 완료하였습니다.");
        } catch (Exception e) {
            resultDto.setSuccess(false);
            resultDto.setMsg("팀가입 신청을 실패하였습니다: " + e.getMessage());
        }
        return resultDto;
    }

    @Override
    public ResultDto leaveTeam(Long teamId, HttpServletRequest servletRequest){
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));

        TeamUser teamUser = teamUserRepository.findByTeamAndUser(team, user);
        if (teamUser == null) {
            throw new RuntimeException("팀에 가입되지 않은 상태입니다.");
        }

        teamUserRepository.delete(teamUser);

        ResultDto resultDto = new ResultDto();
        setSuccess(resultDto);
        return resultDto;
    }

    @Override
    public List<TeamJoinResponseDto> getJoinRequestList(Long teamId, HttpServletRequest servletRequest) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        // 해당 팀 조회
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));

        // 회장 여부 확인
        if (!isTeamLeader(team, user)) {
            throw new RuntimeException("회장만 이 작업을 수행할 수 있습니다.");
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
                .orElseThrow(() -> new RuntimeException("신청서를 찾을 수 없습니다."));

        Team team = request.getTeam();
        // 회장 여부 확인
        if (!isTeamLeader(team, user)) {
            throw new RuntimeException("회장만 이 작업을 수행할 수 있습니다.");
        }

        // 상세 정보 DTO로 반환
        TeamJoinDetailResponseDto detailDto = new TeamJoinDetailResponseDto();
        detailDto.setRequestId(request.getRequestId());
        detailDto.setUserName(request.getUser().getName());
        detailDto.setUserGender(request.getUser().getGender());
        detailDto.setUserAgeRange(request.getUserAgeRange());
        detailDto.setUserPhoneNumber(request.getUserPhoneNumber());
        detailDto.setJoinReason(request.getJoinReason());
        detailDto.setStatus(request.getStatus());
        detailDto.setRole(request.getRole());
        detailDto.setRequestDate(request.getRequestDate());

        return detailDto;
    }

    @Override
    public ResultDto approveJoinRequest(Long requestId, HttpServletRequest servletRequest) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        TeamJoinRequest request = teamJoinRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("신청서를 찾을 수 없습니다."));

        Team team = request.getTeam();
        // 회장 여부 확인
        if (!isTeamLeader(team, user)) {
            throw new RuntimeException("회장만 이 작업을 수행할 수 있습니다.");
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

        ResultDto resultDto = new ResultDto();
        setSuccess(resultDto);
        return resultDto;
    }

    @Override
    public ResultDto rejectJoinRequest(Long requestId, HttpServletRequest servletRequest) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        TeamJoinRequest request = teamJoinRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("신청서를 찾을 수 없습니다."));

        Team team = request.getTeam();
        // 회장 여부 확인
        if (!isTeamLeader(team, user)) {
            throw new RuntimeException("회장만 이 작업을 수행할 수 있습니다.");
        }

        // 가입 요청 삭제
        teamJoinRequestRepository.delete(request);

        ResultDto resultDto = new ResultDto();
        setSuccess(resultDto);
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

    public boolean isTeamLeader(Team team, User user) {
        TeamUser teamUser = teamUserRepository.findByTeamAndUser(team,user);
        return teamUser != null && "회장".equals(teamUser.getRole());
    }
}
