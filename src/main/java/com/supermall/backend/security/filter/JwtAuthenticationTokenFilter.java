package com.supermall.backend.security.filter;

import com.supermall.backend.config.JwtConfig;
import com.supermall.backend.security.util.JwtTokenUtil;
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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT登录授权过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtConfig jwtConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain chain) throws ServletException, IOException {
        String authHeader = request.getHeader(jwtConfig.getHeader());
        if (authHeader != null && authHeader.startsWith(jwtConfig.getTokenPrefix())) {
            String authToken = authHeader.substring(jwtConfig.getTokenPrefix().length()).trim();
            String username = jwtTokenUtil.extractUsername(authToken);
            
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
    }
} 