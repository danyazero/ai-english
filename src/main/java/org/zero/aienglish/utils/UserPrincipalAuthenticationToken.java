package org.zero.aienglish.utils;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.zero.aienglish.model.UserPrincipal;

public class UserPrincipalAuthenticationToken extends AbstractAuthenticationToken {
    private final UserPrincipal principal;

    public UserPrincipalAuthenticationToken(UserPrincipal principal) {
        super(principal.getAuthorities());
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return principal.getPassword();
    }

    @Override
    public UserPrincipal getPrincipal() {
        return principal;
    }
}
