package org.zero.aienglish.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zero.aienglish.config.JwtProperties;
import org.zero.aienglish.model.Credentials;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Setter
@Service
@RequiredArgsConstructor
public class JWTModule {

    private final JwtProperties properties;

    /**
     * Issue session JWT token with email and roles fields.
     * Duration: 1 min
     * Algorithm HMAC256
     *
     * @param userId
     * @return String
     */
    public String issueSession(Integer userId) {
        var now = Instant.now();

        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withIssuedAt(now)
                .withExpiresAt(now.plus(Duration.ofSeconds(properties.getTokenDuration())))// 900 - 15min
                .sign(Algorithm.HMAC256(properties.getSessionTokenSecretKey()));
    }

    /**
     * Decode token by algorithm HMAC256 and provided secretKey
     *
     * @param token     JWT token
     * @return DecodedJWT
     */
    public DecodedJWT decode(String token) throws JWTVerificationException {
        log.info("Provided token: {}", token);
        var decoded = JWT.require(Algorithm.HMAC256(properties.getSessionTokenSecretKey())).build();
        return decoded.verify(token);
    }

}
