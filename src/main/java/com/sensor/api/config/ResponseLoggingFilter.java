package com.sensor.api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Slf4j
@Component
public class ResponseLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingResponseWrapper responseWrapper = responseWrapper(response);
        filterChain.doFilter(request, response);
        logResponse(request, responseWrapper);
    }

    private void logResponse(HttpServletRequest request, ContentCachingResponseWrapper response) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(request.getMethod() + " " + request.getRequestURI() + " - " + response.getStatus());
        builder.append(new String(response.getContentAsByteArray()));
        log.info("RESPONSE DATA : {}", builder);
        response.copyBodyToResponse();
    }

    private ContentCachingResponseWrapper responseWrapper(ServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper responseWrapper) {
            return responseWrapper;
        }
        return new ContentCachingResponseWrapper((HttpServletResponse) response);
    }
}
