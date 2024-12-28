package com.supermall.backend.common.security;

import com.supermall.backend.common.security.util.JwtUtil;
import com.supermall.backend.common.security.model.SecurityUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader("Authorization");
            log.debug("Processing request for path: {}", request.getRequestURI());
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.debug("No Bearer token found in request");
                filterChain.doFilter(request, response);
                return;
            }

            final String jwt = authHeader.substring(7);
            log.debug("Found JWT token: {}", jwt);
            
            // 首先验证 token
            if (!jwtUtil.validateToken(jwt)) {
                log.warn("Invalid JWT token");
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "无效的认证令牌");
                return;
            }

            // 从有效的 token 中获取用户信息
            var claims = jwtUtil.getClaims(jwt);
            String username = claims.getSubject();
            Integer userId = claims.get("userId", Integer.class);
            Integer roleId = claims.get("roleId", Integer.class);
            
            log.debug("Extracted from token - username: {}, userId: {}, roleId: {}", username, userId, roleId);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                    log.debug("Loaded user details for username: {}", username);
                    
                    // 验证用户是否存在且状态正常
                    if (userDetails == null) {
                        log.warn("User not found: {}", username);
                        sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "用户不存在");
                        return;
                    }

                    // 确保 userDetails 是 SecurityUser 类型
                    if (!(userDetails instanceof SecurityUser securityUser)) {
                        log.error("UserDetails is not an instance of SecurityUser");
                        sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "用户认证失败");
                        return;
                    }

                    // 设置用户ID
                    securityUser.setId(userId);

                    // 设置认证信息
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        securityUser,
                        null,
                        securityUser.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Successfully set authentication in SecurityContext for user: {}", username);
                    
                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    log.error("Error loading user details: {}", e.getMessage());
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "用户认证失败");
                }
            } else {
                filterChain.doFilter(request, response);
            }
        } catch (Exception e) {
            log.error("JWT token processing failed: {}", e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "认证失败");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format("{\"success\":false,\"message\":\"%s\",\"data\":null}", message));
    }
} 