package org.zero.aienglish.utils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.zero.aienglish.config.JwtProperties;
import org.zero.aienglish.entity.UserEntity;
import org.zero.aienglish.repository.UserRepository;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;
    private final JWTModule jwtModule;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        log.info("onAuthenticationSuccess");
        var principal = (OAuth2AuthenticationToken) authentication;
        String email = principal.getPrincipal().getAttributes().get("email").toString();

        var founded = userRepository.findFirstByEmail(email);
        if (founded.isPresent()) {
            log.info("This user already has an account with email {} and id -> {}", email, founded.get().getId());

            addSessionTokenAndRedirect(response, founded.get());
            return;
        }

        String givenName = principal.getPrincipal().getAttributes().get("given_name").toString();
        String familyName = principal.getPrincipal().getAttributes().get("family_name").toString();
        String picture = principal.getPrincipal().getAttributes().get("picture").toString();

        var user = UserEntity.builder()
                .firstName(givenName)
                .lastName(familyName)
                .role("USER")
                .email(email)
                .picture(picture)
                .build();
        var createdUser = userRepository.save(user);
        addSessionTokenAndRedirect(response, createdUser);
    }

    private void addSessionTokenAndRedirect(HttpServletResponse response, UserEntity user) throws IOException {
        var createdToken = jwtModule.issueSession(user.getId());
        var cookie = new Cookie("AuthToken", createdToken);
        cookie.setPath("/");
        cookie.setMaxAge(jwtProperties.getTokenDuration());

        response.addCookie(cookie);
        response.sendRedirect("/");
    }
}
