package org.zero.aienglish.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;
import org.zero.aienglish.utils.Authentication;
import org.zero.aienglish.utils.JWTModule;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final Authentication authentication;
    private final JWTModule jwtModule;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        val extractedSessionToken = extractSessionTokenFromRequest(request);
        Optional<DecodedJWT> decodedJWT;
        try {
            decodedJWT = Optional.ofNullable(jwtModule.decode(extractedSessionToken.get()));
        } catch (Exception e) {
            log.info("Error while extraction SessionToken -> {}", e.getMessage());
            decodedJWT = Optional.empty();
        }
        val requestURI = request.getRequestURI();

        if (isTokenPresentAndNotAuthorizationURI(requestURI, extractedSessionToken, decodedJWT)) {
            log.warn("Incorrect session token, skip authentication");
            return;
        }

        logger.info("trying auth user standard method");
        authentication.accept(decodedJWT);

        filterChain.doFilter(request, response);
    }


    private static boolean isTokenPresentAndNotAuthorizationURI(String requestURI, Optional<String> extractedToken, Optional<DecodedJWT> decodedJWT) {
        return extractedToken.isPresent() && decodedJWT.isEmpty() && !requestURI.matches("^/api/v1/auth/[a-z]+$");
    }

    private Optional<String> extractSessionTokenFromRequest(HttpServletRequest request) {
        val cookie = WebUtils.getCookie(request, "AuthToken");
        if (cookie != null) {
            log.info("Cookie: {}", cookie.getValue());
            return Optional.of(cookie.getValue());
        }

        return Optional.empty();
    }
}
