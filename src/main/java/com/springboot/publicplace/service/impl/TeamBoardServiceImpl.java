package com.springboot.publicplace.service.impl;

import com.springboot.publicplace.config.security.JwtTokenProvider;
import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.TeamBoardRequestDto;
import com.springboot.publicplace.dto.response.TeamBoardDetailResponseDto;
import com.springboot.publicplace.entity.*;
import com.springboot.publicplace.repository.TeamBoardRepository;
import com.springboot.publicplace.repository.TeamRepository;
import com.springboot.publicplace.repository.TeamUserRepository;
import com.springboot.publicplace.repository.UserRepository;
import com.springboot.publicplace.service.TeamBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class TeamBoardServiceImpl implements TeamBoardService {
    private final JwtTokenProvider jwtTokenProvider;
    private final TeamBoardRepository teamBoardRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamUserRepository teamUserRepository;

    @Override
    public ResultDto createTeamBoard(HttpServletRequest servletRequest, TeamBoardRequestDto requestDto, Long teamId) {

        ResultDto resultDto = new ResultDto();
        try {
            String token = jwtTokenProvider.resolveToken(servletRequest);
            String email = jwtTokenProvider.getUsername(token);
            User user = userRepository.findByEmail(email);

            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 팀을 찾을 수 없습니다"));

            Boolean teamUser = teamUserRepository.existsByTeamAndUser(team, user);
            if (!teamUser) {
                throw new RuntimeException("팀에 가입되지 않았습니다.");
            }
            TeamBoard teamBoard = new TeamBoard();
            teamBoard.setUser(user);
            teamBoard.setTeam(team);
            teamBoard.setContent(requestDto.getContent());
            teamBoard.setImage(requestDto.getImage());
            teamBoard.setMatchLocation(requestDto.getMatchLocation());

            teamBoardRepository.save(teamBoard);

            resultDto.setSuccess(true);
            resultDto.setMsg("팀 게시글이 성공적으로 작성되었습니다.");
        } catch (Exception e) {
            resultDto.setSuccess(false);
            resultDto.setMsg("팀 게시글 작성에 실패함: " + e.getMessage());
        }
        return resultDto;
    }

    @Override
    public ResultDto updateTeamBoard(HttpServletRequest servletRequest, TeamBoardRequestDto requestDto, Long teamBoardId) {
        ResultDto resultDto = new ResultDto();
        try {
            String token = jwtTokenProvider.resolveToken(servletRequest);
            String email = jwtTokenProvider.getUsername(token);
            User user = userRepository.findByEmail(email);

            TeamBoard teamBoard = teamBoardRepository.getById(teamBoardId);

            if (teamBoard.getUser().equals(user)) {
                teamBoard.setContent(requestDto.getContent());
                teamBoard.setImage(requestDto.getImage());
                teamBoard.setMatchLocation(requestDto.getMatchLocation());

                teamBoardRepository.save(teamBoard);

                resultDto.setMsg("팀 게시글이 성공적으로 수정되었습니다.");
                resultDto.setSuccess(true);
            } else {
                throw new RuntimeException("본인이 작성한 글만 수정 가능합니다.");
            }
        }catch (Exception e){
            resultDto.setSuccess(false);
            resultDto.setMsg("팀 게시글 수정에 실패: " + e.getMessage());
        }
        return resultDto;
    }

    @Override
    public TeamBoardDetailResponseDto getTeamBoardDetail(HttpServletRequest servletRequest, Long teamBoardId) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        TeamBoard teamBoard = teamBoardRepository.getById(teamBoardId);

        Boolean teamUser = teamUserRepository.existsByTeamAndUser(teamBoard.getTeam(), user);
        if (!teamUser) {
            throw new RuntimeException("팀에 가입되지 않았습니다.");
        }

        // 상세 정보 DTO로 반환
        TeamBoardDetailResponseDto detailDto = new TeamBoardDetailResponseDto();
        detailDto.setUserName(teamBoard.getUser().getName());
        detailDto.setCreatedDate(teamBoard.getCreatedAt());
        detailDto.setContent(teamBoard.getContent());
        detailDto.setImage(teamBoard.getImage());
        return detailDto;
    }

    @Override
    public ResultDto deleteTeamBoard(HttpServletRequest servletRequest, Long teamBoardId) {
        ResultDto resultDto = new ResultDto();
        try {
            String token = jwtTokenProvider.resolveToken(servletRequest);
            String email = jwtTokenProvider.getUsername(token);
            User user = userRepository.findByEmail(email);

            TeamBoard teamBoard = teamBoardRepository.getById(teamBoardId);

            if (teamBoard.getUser().equals(user)) {
                teamBoardRepository.delete(teamBoard);
                resultDto.setMsg("팀 게시글이 성공적으로 삭제되었습니다.");
                resultDto.setSuccess(true);
            } else {
                throw new RuntimeException("본인이 작성한 글만 삭제 가능합니다.");
            }
        }catch (Exception e){
            resultDto.setSuccess(false);
            resultDto.setMsg("팀 게시글 삭제에 실패: " + e.getMessage());
        }
        return resultDto;
    }


}
