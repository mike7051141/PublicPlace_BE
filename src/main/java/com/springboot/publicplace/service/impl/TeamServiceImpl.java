package com.springboot.publicplace.service.impl;

import com.springboot.publicplace.config.security.JwtTokenProvider;
import com.springboot.publicplace.dto.MemberDto;
import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.TeamRequestDto;
import com.springboot.publicplace.dto.response.GPTTeamListDto;
import com.springboot.publicplace.dto.response.TeamListResponseDto;
import com.springboot.publicplace.dto.response.TeamResponseDto;
import com.springboot.publicplace.dto.response.TeamRoleResponseDto;
import com.springboot.publicplace.entity.RoleType;
import com.springboot.publicplace.entity.Team;
import com.springboot.publicplace.entity.TeamUser;
import com.springboot.publicplace.entity.User;
import com.springboot.publicplace.exception.DuplicateResourceException;
import com.springboot.publicplace.exception.ResourceNotFoundException;
import com.springboot.publicplace.exception.UnauthorizedActionException;
import com.springboot.publicplace.exception.UserNotInTeamException;
import com.springboot.publicplace.repository.TeamRepository;
import com.springboot.publicplace.repository.TeamUserRepository;
import com.springboot.publicplace.repository.UserRepository;
import com.springboot.publicplace.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

        if(teamRepository.existsByTeamName(teamRequestDto.getTeamName())){
            throw new DuplicateResourceException("팀이름이 중복되었습니다.");
        }

        Team team = new Team();
        team.setTeamName(teamRequestDto.getTeamName());
        team.setTeamImg(teamRequestDto.getTeamImg());
        team.setTeamLocation(teamRequestDto.getTeamLocation());
        team.setLatitude(teamRequestDto.getLatitude());
        team.setLongitude(teamRequestDto.getLongitude());
        team.setTeamInfo(teamRequestDto.getTeamInfo());
        team.setActivityDays(teamRequestDto.getActivityDays());

        teamRepository.save(team);
        // 팀 생성한 유저를 회장으로 등록
        TeamUser teamUser = new TeamUser();
        teamUser.setTeam(team);
        teamUser.setUser(user);
        teamUser.setRole(RoleType.회장); // 역할 설정

        teamUserRepository.save(teamUser);

        ResultDto resultDto = ResultDto.builder()
                .success(true)
                .msg("팀 생성에 성공하였습니다.")
                .code(HttpStatus.OK.value())
                .build();
        return resultDto;
    }

    @Override
    public ResultDto checkTeamName(String teamName) {
        if (teamRepository.existsByTeamName(teamName)) {
            throw new DuplicateResourceException("이미 존재하는 팀명입니다.");
        }
        return ResultDto.builder()
                .success(true)
                .msg("사용 가능한 팀명입니다.")
                .code(HttpStatus.OK.value())
                .build();
    }

    @Override
    public TeamRoleResponseDto checkTeamRole(HttpServletRequest servletRequest, Long teamId) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("팀을 찾을 수 없습니다."));

        TeamUser teamUser = teamUserRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new UserNotInTeamException("팀에 가입된 사용자가 아닙니다."));

        TeamRoleResponseDto teamRoleResponseDto = new TeamRoleResponseDto();
        if (teamUser.getRole().equals(RoleType.회장)) {
            teamRoleResponseDto.setRole(RoleType.회장);
            teamRoleResponseDto.setLeader(true);
        } else if (teamUser.getRole().equals(RoleType.팀원)) {
            teamRoleResponseDto.setRole(RoleType.팀원);
            teamRoleResponseDto.setLeader(false);
        }
        return teamRoleResponseDto;
    }

    @Override
    public ResultDto updateTeam(Long teamId, HttpServletRequest servletRequest, TeamRequestDto teamRequestDto) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        // 팀 정보 가져오기
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("팀을 찾을 수 없습니다."));

        // 회장 여부 확인
        if (!isTeamLeader(team, user)) {
            throw new UnauthorizedActionException("회장만 이 작업을 수행할 수 있습니다.");
        }

        // 팀 정보 업데이트
        team.setTeamName(teamRequestDto.getTeamName());
        team.setTeamImg(teamRequestDto.getTeamImg());
        team.setTeamLocation(teamRequestDto.getTeamLocation());
        team.setLatitude(teamRequestDto.getLatitude());
        team.setLongitude(teamRequestDto.getLongitude());
        team.setTeamInfo(teamRequestDto.getTeamInfo());
        team.setActivityDays(teamRequestDto.getActivityDays());

        teamRepository.save(team);
        ResultDto resultDto = ResultDto.builder()
                .success(true)
                .msg("팀 수정을 완료하였습니다.")
                .code(HttpStatus.OK.value())
                .build();
        return resultDto;
    }

    @Override
    public TeamResponseDto getTeamInfo(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("팀을 찾을 수 없습니다."));

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
                team.getLatitude(),
                team.getLongitude(),
                team.getTeamImg(),
                team.getActivityDays(),
                teamMemberCount,
                members
        );
    }

//    @Override
//    public List<TeamResponseDto> getRandomTeamList(HttpServletRequest servletRequest) {
//        List<Team> teams = teamRepository.findAll();
//        return teams.stream()
//                .map(team -> {
//                    // 팀원 리스트 생성
//                    List<MemberDto> members = team.getTeamUsers().stream()
//                            .map(teamUser -> new MemberDto(
//                                    teamUser.getUser().getName(),
//                                    teamUser.getUser().getNickname(),
//                                    teamUser.getUser().getPosition(),
//                                    teamUser.getRole(),
//                                    teamUser.getUser().getAgeRange()
//                            ))
//                            .collect(Collectors.toList());
//
//                    // 팀원 수 계산
//                    Long teamMemberCount = (long) members.size();
//
//                    // TeamResponseDto 생성 및 반환
//                    return TeamResponseDto.builder()
//                            .teamId(team.getTeamId())
//                            .teamName(team.getTeamName())
//                            .teamInfo(team.getTeamInfo())
//                            .createdAt(team.getCreatedAt())
//                            .teamLocation(team.getTeamLocation())
//                            .latitude(team.getLatitude())
//                            .longitude(team.getLongitude())
//                            .teamImg(team.getTeamImg())
//                            .activityDays(team.getActivityDays())
//                            .teamMemberCount(teamMemberCount)  // 팀원 수 포함
//                            .members(members)  // 팀원 리스트 포함
//                            .build();
//                })
//                .collect(Collectors.toList());
//    }

    @Override
    public List<GPTTeamListDto> getTeamList(HttpServletRequest servletRequest) {
        // 모든 팀을 가져옴
        List<Team> teams = teamRepository.findAll();

        List<GPTTeamListDto> teamLists = teams.stream()
                .map(team -> {
                    // 팀원 수 계산
                    Long teamMemberCount = (long) team.getTeamUsers().size();

                    double averageAge = team.getAverageAge();
                    // GPTTeamListDto 생성 및 반환
                    return GPTTeamListDto.builder()
                            .teamName(team.getTeamName())
                            .teamInfo(team.getTeamInfo())
                            .createdAt(team.getCreatedAt())
                            .teamLocation(team.getTeamLocation())
                            .activityDays(team.getActivityDays())
                            .teamMemberCount(teamMemberCount)
                            .averageAge(averageAge)
                            .build();
                })
                .collect(Collectors.toList());
        return teamLists;
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
                .build())
                .collect(Collectors.toList());
        return teamList;
    }

    public boolean isTeamLeader(Team team, User user) {
        TeamUser teamUser = teamUserRepository.findByTeamAndUser(team,user)
                .orElseThrow(() -> new UnauthorizedActionException("팀에 가입된 사용자가 아닙니다."));
        return teamUser != null && (teamUser.getRole().equals(RoleType.회장));
    }
}
