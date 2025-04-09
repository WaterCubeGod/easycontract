package com.easycontract.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * 流式API端点过滤器
 * 专门用于处理流式API端点的访问权限
 */
public class StreamingEndpointFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 检查是否是流式API端点
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/api/ai/generate/stream")) {
            // 为流式API端点创建一个具有USER角色的匿名认证
            AnonymousAuthenticationToken authentication = new AnonymousAuthenticationToken(
                "anonymous", 
                "anonymousUser", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
            
            // 设置认证到安全上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        filterChain.doFilter(request, response);
    }
}
