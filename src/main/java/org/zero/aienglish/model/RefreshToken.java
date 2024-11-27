package org.zero.aienglish.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Builder
public class RefreshToken {
    private String refreshToken;
    private  String sessionToken;
}