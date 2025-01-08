package com.threemeals.delivery.domain.menu.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.threemeals.delivery.domain.auth.UserPrincipal;
import com.threemeals.delivery.domain.auth.annotation.Authentication;
import com.threemeals.delivery.domain.user.annotation.StoreOwnerOnly;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MenuApiController {


	@StoreOwnerOnly
	@GetMapping("/test")
	public void storeOwnerRoleTest(@Authentication UserPrincipal userPrincipal) {

		log.info("userPrincipal={}", userPrincipal);
		log.info("userPrincipal={}", userPrincipal);
	}
}
