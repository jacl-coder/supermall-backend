package com.supermall.backend.config;

import com.supermall.backend.security.filter.JwtAuthenticationTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import com.supermall.backend.domain.user.service.impl.UserServiceImpl;
import com.supermall.backend.security.util.JwtTokenUtil;
import com.supermall.backend.config.JwtConfig;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserServiceImpl userService;

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter(
            UserServiceImpl userDetailsService,
            JwtTokenUtil jwtTokenUtil,
            JwtConfig jwtConfig) {
        return new JwtAuthenticationTokenFilter(userDetailsService, jwtTokenUtil, jwtConfig);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 公开接口
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/products").permitAll()
                .requestMatchers("/doc.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                
                // 管理员接口
                .requestMatchers("/api/categories/**").hasRole("ADMIN")
                .requestMatchers("/api/brands/**").hasRole("ADMIN")
                .requestMatchers("/api/products/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/orders/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/payments/admin/**").hasRole("ADMIN")
                
                // 商家接口
                .requestMatchers("/api/products/merchant/**").hasRole("MERCHANT")
                .requestMatchers(HttpMethod.POST, "/api/products").hasRole("MERCHANT")
                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("MERCHANT")
                .requestMatchers("/api/orders/merchant/**").hasRole("MERCHANT")
                .requestMatchers("/api/payments/merchant/**").hasRole("MERCHANT")
                
                // 用户接口
                .requestMatchers("/api/cart/**").hasRole("USER")
                .requestMatchers("/api/orders/**").hasAnyRole("USER", "MERCHANT")
                .requestMatchers("/api/payments/**").hasRole("USER")
                
                // 其他接口需要认证
                .anyRequest().authenticated())
            .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
} 