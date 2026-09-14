package com.wilson.inkwell.apigateway.security;

import java.io.IOException;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.wilson.inkwell.apigateway.dto.HttpErrorResponseBody;
import com.wilson.inkwell.apigateway.enums.HttpErrorEnum;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {


        // overrides the BearerTokenEntryPoint's response for a more detailed one
        if (authException instanceof OAuth2AuthenticationException oauthException) {

            int status = HttpStatus.UNAUTHORIZED.value();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(status);
            
            OAuth2Error error = oauthException.getError();

            if(error.getErrorCode().equals("invalid_token")) {

                HttpErrorResponseBody body = new HttpErrorResponseBody(
                    status, 
                    HttpErrorEnum.ACESSS_TOKEN_INVALID, 
                    "no valid access token was provided as a bearer token.", 
                    Instant.now());

                objectMapper.writeValue(response.getWriter(), body);
            }

        }

    }

}
