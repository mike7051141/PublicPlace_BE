package com.springboot.publicplace.service.impl;

import com.springboot.publicplace.config.security.JwtTokenProvider;
import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.TeamBoardCommentRequestDto;
import com.springboot.publicplace.entity.TeamBoard;
import com.springboot.publicplace.entity.TeamBoardComment;
import com.springboot.publicplace.entity.User;
import com.springboot.publicplace.repository.TeamBoardCommentRepository;
import com.springboot.publicplace.repository.TeamBoardRepository;
import com.springboot.publicplace.repository.UserRepository;
import com.springboot.publicplace.service.TeamBoardCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class TeamBoardCommentServiceImpl implements TeamBoardCommentService {
    private final JwtTokenProvider jwtTokenProvider;
    private final TeamBoardCommentRepository teamBoardCommentRepository;
    private final TeamBoardRepository teamBoardRepository;
    private final UserRepository userRepository;

    @Override
    public ResultDto createComment(HttpServletRequest servletRequest, Long teamBoardId, TeamBoardCommentRequestDto requestDto) {
        ResultDto resultDto = new ResultDto();
        try {
            String token = jwtTokenProvider.resolveToken(servletRequest);
            String email = jwtTokenProvider.getUsername(token);
            User user = userRepository.findByEmail(email);

            TeamBoard teamBoard = teamBoardRepository.findById(teamBoardId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 팀 게시글이 존재하지 않습니다."));

            TeamBoardComment comment = new TeamBoardComment();
            comment.setUser(user);
            comment.setTeamBoard(teamBoard);
            comment.setContent(requestDto.getContent());

            teamBoardCommentRepository.save(comment);
            resultDto.setMsg("댓글을 성공적으로 작성하였습니다.");
            resultDto.setSuccess(true);
        } catch (Exception e) {
            resultDto.setSuccess(false);
            resultDto.setMsg("댓글 작성 실패: " + e.getMessage());
        }
        return resultDto;
    }

    @Override
    public ResultDto deleteComment(HttpServletRequest servletRequest, Long commentId) {
        ResultDto resultDto = new ResultDto();
        try {
            String token = jwtTokenProvider.resolveToken(servletRequest);
            String email = jwtTokenProvider.getUsername(token);
            User user = userRepository.findByEmail(email);

            TeamBoardComment comment = teamBoardCommentRepository.findById(commentId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다."));
            if(comment.getUser().equals(user)){

                teamBoardCommentRepository.delete(comment);
                resultDto.setMsg("댓글을 성공적으로 삭제하였습니다.");
                resultDto.setSuccess(true);
            }else {
                resultDto.setSuccess(false);
                resultDto.setMsg("댓글을 삭제할 권한이 없습니다.");
            }
        } catch (Exception e) {
            resultDto.setSuccess(false);
            resultDto.setMsg("댓글 작성 실패: " + e.getMessage());
        }
        return resultDto;
    }
}
