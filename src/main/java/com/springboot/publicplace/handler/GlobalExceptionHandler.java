package com.springboot.publicplace.handler;

import com.springboot.publicplace.dto.ResultDto;
import com.springboot.publicplace.exception.InvalidTokenException;
import com.springboot.publicplace.exception.ResourceNotFoundException;
import com.springboot.publicplace.exception.UnauthorizedActionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 일반적인 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultDto> handleAllExceptions(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        ResultDto resultDto = ResultDto.builder()
                .success(false)
                .msg("알 수 없는 오류가 발생했습니다.")
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
        return new ResponseEntity<>(resultDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ResourceNotFoundException 처리
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResultDto> handleResourceNotFound(ResourceNotFoundException ex) {
        log.error("Resource Not Found: {}", ex.getMessage());
        ResultDto resultDto = ResultDto.builder()
                .success(false)
                .msg("Resource Not Found: " + ex.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .build();
        return new ResponseEntity<>(resultDto, HttpStatus.NOT_FOUND);
    }

    // InvalidTokenException 처리
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ResultDto> handleInvalidToken(InvalidTokenException ex) {
        log.error("Invalid Token: {}", ex.getMessage());
        ResultDto resultDto = ResultDto.builder()
                .success(false)
                .msg("Invalid Token: " + ex.getMessage())
                .code(HttpStatus.UNAUTHORIZED.value())
                .build();
        return new ResponseEntity<>(resultDto, HttpStatus.UNAUTHORIZED);
    }

    // UnauthorizedActionException 처리
    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ResultDto> handleUnauthorizedAction(UnauthorizedActionException ex) {
        log.error("Unauthorized Action: {}", ex.getMessage());
        ResultDto resultDto = ResultDto.builder()
                .success(false)
                .msg("Unauthorized Action: " + ex.getMessage())
                .code(HttpStatus.FORBIDDEN.value())
                .build();
        return new ResponseEntity<>(resultDto, HttpStatus.FORBIDDEN);
    }

    // 유효성 검증 예외 처리 (예: @Valid 어노테이션 사용 시)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResultDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.error("Validation Error: {}", errorMessage);
        ResultDto resultDto = ResultDto.builder()
                .success(false)
                .msg(errorMessage)
                .code(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(resultDto, HttpStatus.BAD_REQUEST);
    }
}