package com.springboot.publicplace.service.impl;

import com.springboot.publicplace.config.security.JwtTokenProvider;
import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.dto.request.CommentRequestDto;
import com.springboot.publicplace.dto.request.PostRequestDto;
import com.springboot.publicplace.dto.response.PostCommentResponseDto;
import com.springboot.publicplace.dto.response.PostDetailResponseDto;
import com.springboot.publicplace.dto.response.PostListResponseDto;
import com.springboot.publicplace.entity.Comment;
import com.springboot.publicplace.entity.LikePost;
import com.springboot.publicplace.entity.Post;
import com.springboot.publicplace.entity.User;
import com.springboot.publicplace.exception.InvalidTokenException;
import com.springboot.publicplace.exception.ResourceNotFoundException;
import com.springboot.publicplace.exception.UnauthorizedActionException;
import com.springboot.publicplace.repository.CommentRepository;
import com.springboot.publicplace.repository.LikePostRepository;
import com.springboot.publicplace.repository.PostRepository;
import com.springboot.publicplace.repository.UserRepository;
import com.springboot.publicplace.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.protocol.HTTP;
import org.slf4j.ILoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikePostRepository likePostRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public ResultDto createPost(HttpServletRequest servletRequest, PostRequestDto postRequestDto) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        // Post 객체 생성 및 데이터 설정
        Post post = Post.builder()
                .user(user)
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .category(postRequestDto.getCategory())
                .postImg(postRequestDto.getPostImg())
                .build();

        // 게시글 저장
        postRepository.save(post);

        // 성공 시 결과 설정
        ResultDto resultDto = ResultDto.builder()
                .success(true)
                .msg("게시글이 성공적으로 작성되었습니다.")
                .code(HttpStatus.OK.value())
                .build();

        return resultDto;
    }

    @Override
    @Transactional
    public ResultDto updatePost(Long postId, HttpServletRequest servletRequest, PostRequestDto postRequestDto) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);;

        // 게시글 조회 또는 예외 던지기
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 게시글을 찾을 수 없습니다."));

        // 작성자 확인 또는 예외 던지기
        if (!post.getUser().equals(user)) {
            throw new UnauthorizedActionException("본인이 작성한 게시글만 수정할 수 있습니다.");
        }

        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        post.setCategory(postRequestDto.getCategory());
        post.setPostImg(postRequestDto.getPostImg());

        // 수정된 게시글 저장
        postRepository.save(post);

        ResultDto resultDto = ResultDto.builder()
                .success(true)
                .msg("게시글이 성공적으로 수정되었습니다.")
                .code(HttpStatus.OK.value())
                .build();
        return resultDto;
    }

    @Override
    @Transactional
    public ResultDto deletePost(Long postId, HttpServletRequest servletRequest) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);;

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 게시글을 찾을 수 없습니다."));

        if (!post.getUser().equals(user)) {
            throw new UnauthorizedActionException("본인이 작성한 게시글만 삭제할 수 있습니다.");
        }

        postRepository.delete(post);
        ResultDto resultDto = ResultDto.builder()
                .success(true)
                .msg("게시글이 성공적으로 삭제되었습니다.")
                .code(HttpStatus.OK.value())
                .build();
        return resultDto;
    }

    public List<PostListResponseDto> getPostsByCategory(String category, int page, String sortBy, String title) {

        Sort sort;
        if(sortBy.equals("views")){
            sort = Sort.by(Sort.Direction.DESC, "viewCount").and(Sort.by(Sort.Direction.DESC, "createdAt"));
        } else if (sortBy.equals("likes")) {
            sort = Sort.by(Sort.Direction.DESC,"liked").and(Sort.by(Sort.Direction.DESC, "createdAt"));
        } else {
            sort = Sort.by(Sort.Direction.DESC,"createdAt");
        }
        Pageable pageable = PageRequest.of(page,10, sort);
        Page<Post> posts;

        // title이 비어있지 않은 경우 제목으로 검색
        if (title != null && !title.isEmpty()) {
            if ("전체".equals(category)) {
                // 카테고리가 전체일 경우 모든 카테고리에서 제목으로 검색
                posts = postRepository.findByTitleContaining(title, pageable);
            } else {
                // 특정 카테고리에서 제목으로 검색
                posts = postRepository.findByTitleContainingAndCategory(title, category, pageable);
            }
        } else if ("전체".equals(category)) {
            posts = postRepository.findAll(pageable);
        } else {
            posts = postRepository.findByCategory(category, pageable);
        }
        Page<PostListResponseDto> postPage = posts.map(post -> PostListResponseDto.builder()
                .postId(post.getPostId())
                .category(post.getCategory())
                .title(post.getTitle())
                .authorNickname(post.getUser().getNickname())
                .viewCount(post.getViewCount())
                .commentCount(post.getComments().size())
                .likeCount(post.getLiked())
                .createdDate(post.getCreatedAt())
                .build());
        List<PostListResponseDto> postList = postPage.getContent();
        return postList;
    }

    @Override
    public PostDetailResponseDto getPostDetails(Long postId, HttpServletRequest servletRequest) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 게시글을 찾을 수 없습니다."));

        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
        List<PostCommentResponseDto> postCommentResponseDtos = post.getComments().stream()
                .map(comment -> {
                    PostCommentResponseDto postCommentResponseDto = new PostCommentResponseDto();
                    postCommentResponseDto.setCommentId(comment.getCommentId());
                    postCommentResponseDto.setProfileImg(comment.getUser().getProfileImg());
                    postCommentResponseDto.setContent(comment.getContent());
                    postCommentResponseDto.setNickname(comment.getUser().getNickname());
                    postCommentResponseDto.setCreatedDate(comment.getCreatedAt());
                    return postCommentResponseDto;
                })
                .collect(Collectors.toList());

        PostDetailResponseDto postDetailResponseDto = new PostDetailResponseDto(
                post.getTitle(),
                post.getCategory(),
                post.getUser().getProfileImg(),
                post.getContent(),
                post.getPostImg(),
                post.getUser().getNickname(),
                post.getCreatedAt(),
                post.getViewCount(),
                post.getLiked(),
                post.getComments().size(),
                postCommentResponseDtos
                );
        return postDetailResponseDto;
    }

    @Override
    public ResultDto createComment(HttpServletRequest servletRequest, CommentRequestDto commentRequestDto) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);

        Post post = postRepository.findById(commentRequestDto.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("해당 게시글을 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .content(commentRequestDto.getContent())
                .user(user)
                .post(post)
                .build();

        commentRepository.save(comment);

        ResultDto resultDto = ResultDto.builder()
                .success(true)
                .msg("댓글이 성공적으로 작성되었습니다.")
                .code(HttpStatus.OK.value())
                .build();
        return resultDto;
    }

    @Override
    public ResultDto deleteComment(Long commentId, HttpServletRequest servletRequest) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);;

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 댓글을 찾을 수 없습니다."));

        // 댓글 작성자 & 게시글 작성자만 댓글 삭제 가능
        if (!comment.getUser().equals(user) || comment.getPost().getUser().equals(user)) {
            throw new UnauthorizedActionException("댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);
        ResultDto resultDto = ResultDto.builder()
                .success(true)
                .msg("댓글이 삭제되었습니다.")
                .code(HttpStatus.OK.value())
                .build();
        return resultDto;
    }

    @Override
    public ResultDto toggleLike(Long postId, HttpServletRequest servletRequest) {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        String email = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByEmail(email);;

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 게시글을 찾을 수 없습니다."));

        LikePost existingLike = likePostRepository.findByUserAndPost(user, post);

        if (existingLike != null) {
            likePostRepository.delete(existingLike);
            post.setLiked(post.getLiked() - 1);
            return ResultDto.builder()
                    .success(true)
                    .msg("좋아요가 취소되었습니다.")
                    .code(HttpStatus.OK.value())
                    .build();
        } else {
            LikePost likePost = new LikePost();
            likePost.setUser(user);
            likePost.setPost(post);
            likePostRepository.save(likePost);
            post.setLiked(post.getLiked() + 1);
            return ResultDto.builder()
                    .success(true)
                    .msg("좋아요가 추가되었습니다.")
                    .code(HttpStatus.OK.value())
                    .build();
        }
    }
}
