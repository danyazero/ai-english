package org.zero.aienglish.controller;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@Slf4j
@RestController
public class TestController {

    @GetMapping
    public UserDetails test(@AuthenticationPrincipal OAuth2User principal) {
        log.info("principal: {}", principal);

            String givenName = principal.getAttributes().get("given_name").toString();
            String familyName = principal.getAttributes().get("family_name").toString();
            String email = principal.getAttributes().get("email").toString();
            String picture = principal.getAttributes().get("picture").toString();

            return UserDetails.builder()
                    .email(email)
                    .firstName(givenName)
                    .lastName(familyName)
                    .picture(picture)
                    .build();
    }

    @Builder
    protected record UserDetails(String firstName, String lastName, String email, String picture) {}
}
