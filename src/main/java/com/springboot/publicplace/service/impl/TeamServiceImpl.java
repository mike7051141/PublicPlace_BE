package com.springboot.publicplace.service.impl;

import com.springboot.publicplace.config.security.JwtTokenProvider;
import com.springboot.publicplace.dto.CommonResponse;
import com.springboot.publicplace.dto.MemberDto;
import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.TeamRequestDto;
import com.springboot.publicplace.dto.response.TeamResponseDto;
import com.springboot.publicplace.entity.Team;
import com.springboot.publicplace.entity.TeamUser;
import com.springboot.publicplace.entity.User;
import com.springboot.publicplace.repository.TeamRepository;
import com.springboot.publicplace.repository.TeamUserRepository;
import com.springboot.publicplace.repository.UserRepository;
import com.springboot.publicplace.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final JwtTokenProvider jwtTokenProvider;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamUserRepository teamUserRepository;

    @Override
    public ResultDto createTeam(HttpServletRequest servletRequest, TeamRequestDto teamRequestDto) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);


        ResultDto resultDto = new ResultDto();

        if(jwtTokenProvider.validationToken(token)){
            Team team = new Team();
            team.setTeamName(teamRequestDto.getTeamName());
            team.setTeamImg(teamRequestDto.getTeamImg());
            team.setTeamLocation(teamRequestDto.getTeamLocation());
            team.setTeamInfo(teamRequestDto.getTeamInfo());
            team.setActivityDays(teamRequestDto.getActivityDays());

            teamRepository.save(team);
            // 팀 생성한 유저를 회장으로 등록
            TeamUser teamUser = new TeamUser();
            teamUser.setTeam(team);
            teamUser.setUser(user);
            teamUser.setRole("회장"); // 역할 설정

            teamUserRepository.save(teamUser);
            setSuccess(resultDto);
        }else {
            setFail(resultDto);
        }
        return resultDto;
    }

    @Override
    public ResultDto updateTeam(Long teamId, HttpServletRequest servletRequest, TeamRequestDto teamRequestDto) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        ResultDto resultDto = new ResultDto();

        if (jwtTokenProvider.validationToken(token)) {
            // 팀 정보 가져오기
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));

            // 현재 유저가 해당 팀의 회장인지 확인
            TeamUser teamUser = teamUserRepository.findByTeamAndUser(team, user)
                    .orElseThrow(() -> new RuntimeException("해당 팀에 소속되지 않았거나 권한이 없습니다."));

            if (!teamUser.getRole().equals("회장")) {
                throw new RuntimeException("팀 정보를 수정할 권한이 없습니다.");
            }

            // 팀 정보 업데이트
            team.setTeamName(teamRequestDto.getTeamName());
            team.setTeamImg(teamRequestDto.getTeamImg());
            team.setTeamLocation(teamRequestDto.getTeamLocation());
            team.setTeamInfo(teamRequestDto.getTeamInfo());
            team.setActivityDays(teamRequestDto.getActivityDays());

            teamRepository.save(team);
            setSuccess(resultDto);
        } else {
            setFail(resultDto);
        }
        return resultDto;
    }


    @Override
    public ResultDto joinTeam(Long teamId, HttpServletRequest servletRequest) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));

        boolean alreadyJoined = teamUserRepository.existsByTeamAndUser(team, user);
        if (alreadyJoined) {
            throw new RuntimeException("이미 팀에 가입된 유저입니다.");
        }
        // 팀 가입 로직
        TeamUser teamUser = new TeamUser();
        teamUser.setTeam(team);
        teamUser.setUser(user);
        teamUser.setRole("팀원");  // 기본 역할을 '팀원'으로 설정

        teamUserRepository.save(teamUser);

        // 성공 결과 반환
        ResultDto resultDto = new ResultDto();
        setSuccess(resultDto);
        return resultDto;
    }

    @Override
    public TeamResponseDto getTeamInfo(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));

        // 팀원 리스트 가져오기
        List<MemberDto> members = team.getTeamUsers().stream()
                .map(teamUser -> new MemberDto(
                        teamUser.getUser().getName(),
                        teamUser.getUser().getNickname(),
                        teamUser.getUser().getPosition(),
                        teamUser.getRole(),
                        teamUser.getUser().getAgeRange()
                ))
                .collect(Collectors.toList());

        // 팀원 수 계산
        Long teamMemberCount = (long) members.size();

        return new TeamResponseDto(
                team.getTeamName(),
                team.getTeamInfo(),
                team.getCreatedAt(),
                team.getTeamLocation(),
                team.getTeamImg(),
                team.getActivityDays(),
                teamMemberCount,
                members
        );
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
