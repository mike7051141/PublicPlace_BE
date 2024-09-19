package com.springboot.publicplace.service.impl;

import com.springboot.publicplace.config.security.JwtTokenProvider;
import com.springboot.publicplace.dto.CommonResponse;
import com.springboot.publicplace.dto.MemberDto;
import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.TeamRequestDto;
import com.springboot.publicplace.dto.response.TeamListResponseDto;
import com.springboot.publicplace.dto.response.TeamResponseDto;
import com.springboot.publicplace.entity.Team;
import com.springboot.publicplace.entity.TeamUser;
import com.springboot.publicplace.entity.User;
import com.springboot.publicplace.repository.TeamRepository;
import com.springboot.publicplace.repository.TeamUserRepository;
import com.springboot.publicplace.repository.UserRepository;
import com.springboot.publicplace.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
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

            // 회장 여부 확인
            if (!isTeamLeader(team, user)) {
                throw new RuntimeException("회장만 이 작업을 수행할 수 있습니다.");
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
                team.getTeamId(),
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

    @Override
    public List<TeamResponseDto> getTeamList(HttpServletRequest servletRequest) {
        List<Team> teams = teamRepository.findAll();
        return teams.stream()
                .map(team -> {
                    // 팀원 리스트 생성
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

                    // TeamResponseDto 생성 및 반환
                    return TeamResponseDto.builder()
                            .teamId(team.getTeamId())
                            .teamName(team.getTeamName())
                            .teamInfo(team.getTeamInfo())
                            .createdAt(team.getCreatedAt())
                            .teamLocation(team.getTeamLocation())
                            .teamImg(team.getTeamImg())
                            .activityDays(team.getActivityDays())
                            .teamMemberCount(teamMemberCount)  // 팀원 수 포함
                            .members(members)  // 팀원 리스트 포함
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<TeamListResponseDto> getTeamsByCriteria(String sortBy, String teamName) {
        List<Team> teams;

        // 검색어가 있을 경우 팀명으로 검색, 없으면 전체 팀 목록 조회
        if (teamName != null && !teamName.isEmpty()) {
            teams = teamRepository.findByTeamNameContaining(teamName);
        } else {
            // 검색어가 없을 경우 전체 팀 목록 조회
            teams = teamRepository.findAll();
        }

        // 정렬 기준에 따라 처리
        if (sortBy.equals("averageAge")) {
            // 평균 나이 기준으로 정렬
            teams = teams.stream()
                    .sorted(Comparator.comparingDouble(Team::getAverageAge)
                            .thenComparing(Team::getCreatedAt, Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        } else if (sortBy.equals("memberCount")) {
            // 팀 멤버 수 기준으로 정렬
            teams = teams.stream()
                    .sorted(Comparator.comparingLong(Team::getTeamMembers).reversed()
                            .thenComparing(Team::getCreatedAt, Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        } else if (sortBy.equals("oldest")) {
            // 창단연도 기준 오름차순 정렬
            teams = teams.stream()
                    .sorted(Comparator.comparing(Team::getCreatedAt))
                    .collect(Collectors.toList());
        } else {
            // 기본: 창단연도 기준 내림차순 정렬
            teams = teams.stream()
                    .sorted(Comparator.comparing(Team::getCreatedAt).reversed())
                    .collect(Collectors.toList());
        }

        // Team 엔티티에서 TeamListResponseDto로 변환
        List<TeamListResponseDto> teamList = teams.stream().map(team -> TeamListResponseDto.builder()
                .teamId(team.getTeamId())
                .teamName(team.getTeamName())
                .createdAt(team.getCreatedAt())
                .teamLocation(team.getTeamLocation())
                .teamImg(team.getTeamImg())
                .teamMemberCount(team.getTeamMembers()) // 팀 멤버 수
                .averageAge(team.getAverageAge())       // 평균 나이
                .build()).collect(Collectors.toList());

        return teamList;
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
