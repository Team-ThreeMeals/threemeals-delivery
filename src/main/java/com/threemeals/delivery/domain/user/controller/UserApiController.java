package com.threemeals.delivery.domain.user.controller;

import org.springframework.web.bind.annotation.RestController;

import com.threemeals.delivery.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;




}
