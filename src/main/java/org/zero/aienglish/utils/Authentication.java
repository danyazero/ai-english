package org.zero.aienglish.utils;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class Authentication implements Consumer<Optional<DecodedJWT>> {
    private final JwtToPrincipalConverter jwtToPrincipalConverter;

    @Override
    public void accept(Optional<DecodedJWT> decodedJWT) {
        decodedJWT.map(jwtToPrincipalConverter::convert).map(UserPrincipalAuthenticationToken::new)
                .ifPresent(authentication -> SecurityContextHolder.getContext().setAuthentication(authentication));
    }

/*
    public Optional<DecodedJWT> decodeSessionToken(Optional<String> token) {
        return token.map(s -> jwtService.decode(s, jwtProperties.getSessionTokenSecretKey()));
    }
*/
}
