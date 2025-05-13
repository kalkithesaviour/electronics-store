package com.vishal.electronicsstore.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.vishal.electronicsstore.service.CustomUserDetailsService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtHelper jwtHelper;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public JwtAuthenticationFilter(
            JwtHelper jwtHelper,
            CustomUserDetailsService customUserDetailsService) {
        this.jwtHelper = jwtHelper;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(
            @NonNull HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.equals("/auth/login")
                || path.equals("/auth/login-with-google")
                || path.equals("/auth/regenerate-jwt-token");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String requestHeader = request.getHeader("Authorization");
        log.info("Header: " + requestHeader);

        String username = null;
        String token = null;
        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            token = requestHeader.substring(7);
            try {
                username = jwtHelper.getUsernameFromToken(token);
                log.info("Username: " + username);
            } catch (IllegalArgumentException e) {
                log.error("Invalid token! " + e.getMessage());
            } catch (ExpiredJwtException e) {
                log.error("Expired JWT! " + e.getMessage());
            } catch (MalformedJwtException e) {
                log.error("Malformed token! " + e.getMessage());
            } catch (Exception e) {
                log.error("Unexpected error while parsing the token! ", e);
            }
        } else {
            log.warn("Invalid JWT Header!");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            if (username.equals(userDetails.getUsername()) && !jwtHelper.isTokenExpired(token)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

}
