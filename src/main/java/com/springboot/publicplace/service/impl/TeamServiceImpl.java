package com.springboot.publicplace.service.impl;

import com.springboot.publicplace.config.security.JwtTokenProvider;
import com.springboot.publicplace.dto.CommonResponse;
import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.TeamRequestDto;
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
