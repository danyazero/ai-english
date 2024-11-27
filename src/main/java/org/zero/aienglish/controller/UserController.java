package org.zero.aienglish.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zero.aienglish.entity.UserEntity;
import org.zero.aienglish.model.UserDetails;
import org.zero.aienglish.model.UserPrincipal;
import org.zero.aienglish.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/user")
public class UserController {
    private final UserService userService;

    @GetMapping
    public UserDetails getUser(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return userService.getUserData(user.getId());
    }

}
