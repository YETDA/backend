package com.funding.backend.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    @GetMapping("/logout")
    public void logout() {
        System.out.println("logout");
    }

    @GetMapping("/me")
    public void me() {
        System.out.println("me");
    }
}
