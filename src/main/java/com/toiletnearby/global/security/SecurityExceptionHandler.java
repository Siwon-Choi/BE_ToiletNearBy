package com.toiletnearby.global.security;

import org.jspecify.annotations.NonNull;
import tools.jackson.databind.ObjectMapper;
import com.toiletnearby.global.exception.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Component
@RequiredArgsConstructor
// Spring Security 인증/인가 실패를 일관된 JSON 응답으로 바꿔주는 핸들러
public class SecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    // Java 객체를 JSON 문자열로 변환을 도와준다.
    private final ObjectMapper objectMapper;

    // 인증 실패 처리: 인증하지 못했을 경우 401을 응답한다.
    @Override
    public void commence(HttpServletRequest request,
                         @NonNull HttpServletResponse response,
                         @NonNull AuthenticationException authException
    ) throws IOException {
        writeErrorResponse(
                response,
                HttpStatus.UNAUTHORIZED,
                "인증이 필요합니다.",
                request.getRequestURI()
        );
    }

    // 인가 실패 처리: 인증은 됐지만 권한이 부족한 경우 403을 응답한다.
    @Override
    public void handle(HttpServletRequest request,
                       @NonNull HttpServletResponse response,
                       @NonNull AccessDeniedException accessDeniedException
    ) throws IOException {
        writeErrorResponse(
                response,
                HttpStatus.FORBIDDEN,
                "접근 권한이 없습니다.",
                request.getRequestURI()
        );
    }

    // 공통 에러 응답을 JSON으로 작성한다.
    private void writeErrorResponse(
            HttpServletResponse response,
            HttpStatus status,
            String message,
            String path
    ) throws IOException {
        ErrorResponseDto errorResponse = ErrorResponseDto.of(status, message, path);

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}