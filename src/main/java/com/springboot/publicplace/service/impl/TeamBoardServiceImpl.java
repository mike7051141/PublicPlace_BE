package com.springboot.publicplace.service.impl;

import com.springboot.publicplace.config.security.JwtTokenProvider;
import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.TeamBoardRequestDto;
import com.springboot.publicplace.dto.response.TeamBoardCommentResponseDto;
import com.springboot.publicplace.dto.response.TeamBoardDetailResponseDto;
import com.springboot.publicplace.dto.response.TeamBoardListResponseDto;
import com.springboot.publicplace.entity.*;
import com.springboot.publicplace.exception.ResourceNotFoundException;
import com.springboot.publicplace.exception.UnauthorizedActionException;
import com.springboot.publicplace.exception.UserNotInTeamException;
import com.springboot.publicplace.repository.*;
import com.springboot.publicplace.service.TeamBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TeamBoardServiceImpl implements TeamBoardService {
    private final JwtTokenProvider jwtTokenProvider;
    private final TeamBoardRepository teamBoardRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamUserRepository teamUserRepository;
    private final TeamBoardCommentRepository teamBoardCommentRepository;

    @Override
    public ResultDto createTeamBoard(HttpServletRequest servletRequest, TeamBoardRequestDto requestDto, Long teamId) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 팀을 찾을 수 없습니다"));

        Boolean teamUser = teamUserRepository.existsByTeamAndUser(team, user);
        if (!teamUser) {
            throw new UserNotInTeamException("팀에 가입되지 않았습니다.");
        }

        TeamBoard teamBoard = new TeamBoard();
        teamBoard.setUser(user);
        teamBoard.setTeam(team);
        teamBoard.setContent(requestDto.getContent());
        teamBoard.setImage(requestDto.getImage());
        teamBoard.setMatchLocation(requestDto.getMatchLocation());
        teamBoard.setLatitude(requestDto.getLatitude());
        teamBoard.setLongitude(requestDto.getLongitude());

        teamBoardRepository.save(teamBoard);

        ResultDto resultDto = ResultDto.builder()
                .success(true)
                .msg("팀 게시글이 성공적으로 작성되었습니다.")
                .code(HttpStatus.OK.value())
                .build();
        return resultDto;
    }

    @Override
    public ResultDto updateTeamBoard(HttpServletRequest servletRequest, TeamBoardRequestDto requestDto, Long teamBoardId) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        TeamBoard teamBoard = teamBoardRepository.findById(teamBoardId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 게시글을 찾을 수 없습니다."));


        if (!teamBoard.getUser().equals(user)) {
            throw new UnauthorizedActionException("본인이 작성한 글만 수정 가능합니다.");
        }

        teamBoard.setContent(requestDto.getContent());
        teamBoard.setImage(requestDto.getImage());
        teamBoard.setMatchLocation(requestDto.getMatchLocation());
        teamBoard.setLongitude(requestDto.getLongitude());
        teamBoard.setLatitude(requestDto.getLatitude());

        teamBoardRepository.save(teamBoard);

        ResultDto resultDto = ResultDto.builder()
                .success(true)
                .msg("팀 게시글이 성공적으로 수정되었습니다.")
                .code(HttpStatus.OK.value())
                .build();
        return resultDto;
    }

    @Override
    public TeamBoardDetailResponseDto getTeamBoardDetail(HttpServletRequest servletRequest, Long teamBoardId) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        TeamBoard teamBoard = teamBoardRepository.findById(teamBoardId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 게시글을 찾을 수 없습니다."));


        Boolean teamUser = teamUserRepository.existsByTeamAndUser(teamBoard.getTeam(), user);
        if (!teamUser) {
            throw new UserNotInTeamException("팀에 가입되지 않았습니다.");
        }
        // 팀 게시글 댓글 조회
        List<TeamBoardCommentResponseDto> teamBoardCommentResponseDtos = teamBoardCommentRepository
                .findByTeamBoard_TeamBoardIdOrderByCreatedAt(teamBoardId)
                .stream()
                .map(comment -> new TeamBoardCommentResponseDto(
                        comment.getCommentId(),
                        comment.getContent(),
                        comment.getUser().getName(),
                        comment.getUser().getProfileImg(),
                        comment.getCreatedAt()
                ))
                .collect(Collectors.toList());

        // 상세 정보 DTO로 반환
        TeamBoardDetailResponseDto detailDto = new TeamBoardDetailResponseDto(
                teamBoard.getTeamBoardId(),
                teamBoard.getContent(),
                teamBoard.getImage(),
                teamBoard.getUser().getUsername(),
                teamBoard.getMatchLocation(),
                teamBoard.getLongitude(),
                teamBoard.getLatitude(),
                teamBoard.getCreatedAt(),
                teamBoardCommentResponseDtos
        );
        return detailDto;
    }

    @Override
    public List<TeamBoardListResponseDto> getTeamBoardList(HttpServletRequest servletRequest, Long teamId, String content, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<TeamBoard> teamBoards;

        if (content == null || content.isEmpty()) {
            teamBoards = teamBoardRepository.findByTeam_TeamId(teamId, pageable);
        } else {
            teamBoards = teamBoardRepository.findByTeam_TeamIdAndContentContaining(teamId, content, pageable);
        }
        List<TeamBoardListResponseDto> teamBoardList = teamBoards.stream().map(teamBoard -> new TeamBoardListResponseDto(
                teamBoard.getTeamBoardId(),
                teamBoard.getContent(),
                teamBoard.getComments().size(),
                teamBoard.getImage(),
                teamBoard.getUser().getName(),
                teamBoard.getUser().getProfileImg(),
                teamBoard.getMatchLocation(),
                teamBoard.getLatitude(),
                teamBoard.getLongitude(),
                teamBoard.getCreatedAt()
        )).collect(Collectors.toList());

        return teamBoardList;
    }

    @Override
    public ResultDto deleteTeamBoard(HttpServletRequest servletRequest, Long teamBoardId) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        TeamBoard teamBoard = teamBoardRepository.findById(teamBoardId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 게시글을 찾을 수 없습니다."));

        // 팀에서의 역할을 확인
        TeamUser teamUser = teamUserRepository.findByTeamAndUser(teamBoard.getTeam(), user)
                .orElseThrow(() -> new UnauthorizedActionException("팀에 가입된 사용자가 아닙니다."));

        // 본인이 작성한 글이 아니거나 회장일 때만 삭제 가능
        if (!teamBoard.getUser().equals(user) && !"회장".equals(teamUser.getRole())) {
            throw new UnauthorizedActionException("본인이 작성한 글만 삭제 가능합니다.");
        }

        teamBoardRepository.delete(teamBoard);
        ResultDto resultDto = ResultDto.builder()
                .success(true)
                .msg("팀 게시글이 성공적으로 삭제되었습니다.")
                .code(HttpStatus.OK.value())
                .build();
        return resultDto;
    }
}
