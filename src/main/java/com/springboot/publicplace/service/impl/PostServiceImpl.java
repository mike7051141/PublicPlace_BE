package com.springboot.publicplace.service.impl;

import com.springboot.publicplace.config.security.JwtTokenProvider;
import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.PostRequestDto;
import com.springboot.publicplace.dto.request.TeamRequestDto;
import com.springboot.publicplace.entity.Post;
import com.springboot.publicplace.entity.Team;
import com.springboot.publicplace.entity.User;
import com.springboot.publicplace.repository.PostRepository;
import com.springboot.publicplace.repository.UserRepository;
import com.springboot.publicplace.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public ResultDto createPost(HttpServletRequest servletRequest, PostRequestDto postRequestDto) {
        // ResultDto 객체 초기화
        ResultDto resultDto = new ResultDto();

        try {
            // 토큰에서 사용자 이메일 추출
            String token = jwtTokenProvider.resolveToken(servletRequest);
            String email = jwtTokenProvider.getUsername(token);
            User user = userRepository.findByEmail(email);

            // 토큰 유효성 검증
            if (jwtTokenProvider.validationToken(token)) {
                // Post 객체 생성 및 데이터 설정
                Post post = new Post();
                post.setTitle(postRequestDto.getTitle());
                post.setContent(postRequestDto.getContent());
                post.setCategory(postRequestDto.getCategory());
                post.setPostImg(postRequestDto.getPostImg());
                post.setUser(user); // 게시글 작성자 설정

                // 게시글 저장
                postRepository.save(post);

                // 성공 시 결과 설정
                resultDto.setSuccess(true);
                resultDto.setMsg("게시글이 성공적으로 작성되었습니다.");
            }
        } catch (Exception e) {
            // 예외 발생 시 실패 결과 설정
            resultDto.setSuccess(false);
            resultDto.setMsg("게시글 작성에 실패했습니다: " + e.getMessage());
        }
        return resultDto;
    }

    @Override
    public ResultDto updatePost(Long postId, HttpServletRequest servletRequest, PostRequestDto postRequestDto) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        ResultDto resultDto = new ResultDto();

        if(jwtTokenProvider.validationToken(token)){
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));
            if (post.getUser().equals(user)) {
                post.setTitle(postRequestDto.getTitle());
                post.setContent(postRequestDto.getContent());
                post.setCategory(postRequestDto.getCategory());
                post.setPostImg(postRequestDto.getPostImg());

                // 수정된 게시글 저장
                postRepository.save(post);

                // 성공 시 결과 설정
                resultDto.setSuccess(true);
                resultDto.setMsg("게시글이 성공적으로 수정되었습니다.");
            } else {
                // 작성자가 아닌 경우
                resultDto.setSuccess(false);
                resultDto.setMsg("본인이 작성한 게시글만 수정할 수 있습니다.");
            }
        }
        return resultDto;
    }

    @Override
    public ResultDto deletePost(Long postId, HttpServletRequest servletRequest) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        ResultDto resultDto = new ResultDto();

        if(jwtTokenProvider.validationToken(token)){
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

            if (post.getUser().equals(user)) {

                // 게시글 삭제
                postRepository.delete(post);

                // 성공 시 결과 설정
                resultDto.setSuccess(true);
                resultDto.setMsg("게시글이 성공적으로 삭제되었습니다.");
            } else {
                // 작성자가 아닌 경우
                resultDto.setSuccess(false);
                resultDto.setMsg("본인이 작성한 게시글만 삭제할 수 있습니다.");
            }
        }
        return resultDto;
    }
}
