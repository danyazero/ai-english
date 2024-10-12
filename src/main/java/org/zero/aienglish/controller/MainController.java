package org.zero.aienglish.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api")
@RequiredArgsConstructor
public class MainController {

    @GetMapping
    public String testEndpoint() {
        return "Hello world";
    }
}
