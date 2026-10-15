package com.hotelmanagement.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String jsonResponse = """
            {
                "status": 401,
                "error": "Unauthorized / Access Denied (HTTP 403/401)",
                "message": "You must provide a valid JWT Bearer Token in the HTTP Authorization header.",
                "howToFix": "1. Send a POST request to /api/auth/login with email and password. 2. Copy the token. 3. Pass header 'Authorization: Bearer <your_token>' in Postman.",
                "path": "%s"
            }
            """.formatted(request.getRequestURI());

        response.getWriter().write(jsonResponse);
    }
}
