package com.stackoverflow.beta.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackoverflow.beta.service.impl.UserServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserServiceImpl userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserServiceImpl userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Get the URI of the request
        String uri = request.getRequestURI();

        // Skip JWT validation for public endpoints like /register or Swagger
        if (uri.startsWith("/user/register") || uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs")) {
            System.out.println("Skipping JWT validation for URI: " + uri);  // Log skipping message
            filterChain.doFilter(request, response);
            return;
        }

        try{
            // Proceed with JWT validation for other endpoints
            String header = request.getHeader("Authorization");

            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                String username = jwtUtil.extractUsername(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userService.loadUserByUsername(username);

                    if (jwtUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }

            filterChain.doFilter(request, response); // Continue the filter chain
        } catch (ExpiredJwtException ex) {
            handleJwtException(response, "Token expired", HttpStatus.UNAUTHORIZED);
        } catch (JwtException ex) {
            handleJwtException(response, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
        } catch (Exception e){
            e.printStackTrace();  // print real error
            handleJwtException(response, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private void handleJwtException(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        if (response.isCommitted()) {
            // If response is already committed, don’t try to write again
            return;
        }

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("message", message);
        body.put("status", status.value());

        byte[] json = objectMapper.writeValueAsBytes(body);

        // ✅ Use getOutputStream() — not getWriter()
        ServletOutputStream out = response.getOutputStream();
        out.write(json);
        out.flush();
    }
}
