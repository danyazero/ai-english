package org.zero.aienglish.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class Credentials {
    private Long userId;
    private List<String> roles;
}