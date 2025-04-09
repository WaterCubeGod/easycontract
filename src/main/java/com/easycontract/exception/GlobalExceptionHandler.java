package com.easycontract.exception;

import com.easycontract.entity.vo.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ResponseEntity<Response<?>> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Response.fail("权限不足: " + ex.getMessage()));
    }
    
    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public ResponseEntity<Response<?>> handleAuthenticationException(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Response.fail("认证失败: " + ex.getMessage()));
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Response<?>> handleGenericException(Exception ex) {
        // 打印异常堆栈以便调试
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.fail("服务器错误: " + ex.getMessage()));
    }
}
