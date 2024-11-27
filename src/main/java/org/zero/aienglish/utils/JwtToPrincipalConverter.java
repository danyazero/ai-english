package org.zero.aienglish.utils;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.zero.aienglish.model.UserPrincipal;
import org.zero.aienglish.repository.UserRepository;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtToPrincipalConverter {
    private final UserRepository userRepository;

    public UserPrincipal convert(DecodedJWT jwt) {
        var user = userRepository.findById(Integer.parseInt(jwt.getSubject()));
        if (user.isEmpty()) return null;

        var authorityList = Arrays.stream(user.get().getRole().split(", "))
                .map(SimpleGrantedAuthority::new).toList();

        for (SimpleGrantedAuthority simpleGrantedAuthority : authorityList) {
            System.out.print(simpleGrantedAuthority);
        }

        return UserPrincipal.builder()
                .id( user.get().getId() )
                .firstName( user.get().getFirstName() )
                .lastName( user.get().getLastName() )
                .authorities( authorityList )
                .email(user.get().getEmail())
                .build();
    }
}
