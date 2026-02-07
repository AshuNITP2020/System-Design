package com.challenge.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter.
 * This filter intercepts every HTTP request and validates JWT tokens.
 * 
 * How it works:
 * 1. Extract JWT token from "Authorization: Bearer <token>" header
 * 2. Validate the token
 * 3. Load user details from database
 * 4. Set authentication in SecurityContext
 * 5. Continue with the filter chain
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Filter method that runs once per request.
     * Extracts and validates JWT token, then sets authentication in SecurityContext.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Extract token from request header
        String token = getTokenFromRequest(request);

        // If token exists and is valid, authenticate the user
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // Extract username from token
            String username = jwtTokenProvider.getUsernameFromToken(token);

            // Load user details from database
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Create authentication object
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,  // credentials are null for JWT (token is the credential)
                    userDetails.getAuthorities()
                );

            // Set additional details (IP address, session ID, etc.)
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set authentication in SecurityContext
            // This makes the user "authenticated" for this request
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Continue with the filter chain
        // If no token or invalid token, SecurityContext remains empty
        // Spring Security will handle unauthorized requests later
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header.
     * Expected format: "Authorization: Bearer <token>"
     * 
     * @param request HTTP request
     * @return JWT token string, or null if not found
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            // Remove "Bearer " prefix (7 characters)
            return bearerToken.substring(7);
        }
        
        return null;
    }
}

