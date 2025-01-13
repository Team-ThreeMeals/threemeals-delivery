package com.threemeals.delivery.domain.oauth;

import com.threemeals.delivery.config.MyInfoConfig;
import com.threemeals.delivery.domain.auth.dto.request.SignupRequestDto;
import com.threemeals.delivery.domain.auth.dto.response.LoginResponseDto;
import com.threemeals.delivery.domain.auth.dto.response.SignupResponseDto;
import com.threemeals.delivery.domain.auth.service.AuthService;
import com.threemeals.delivery.domain.oauth.dto.OAuthResponseDto;
import com.threemeals.delivery.domain.oauth.service.OAuthUserService;
import com.threemeals.delivery.domain.oauth.util.UserPrincipal;
import com.threemeals.delivery.domain.user.dto.response.UserResponseDto;
import com.threemeals.delivery.domain.user.entity.User;
import com.threemeals.delivery.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final UserService userService;
    private final AuthService authService;
    private final MyInfoConfig myInfoConfig;
    private final OAuthUserService oauthService;

    @GetMapping("/my-info")
    public ResponseEntity<UserResponseDto> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userService.getUserById(userPrincipal.getId());
        return ResponseEntity.ok(UserResponseDto.fromEntity(user));
    }

    @GetMapping("/login/success")
    public ResponseEntity<OAuthResponseDto> loginSuccess(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(new OAuthResponseDto(true, "Login successful",null));
    }

    @PostMapping("/login/fail")
    public ResponseEntity<OAuthResponseDto> loginFailure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new OAuthResponseDto(false, "Login failed",null));
    }
    @GetMapping("/login/naver")
    public void oauthLogin(HttpServletResponse response) throws IOException {
        String state = UUID.randomUUID().toString();
        String authorizationUrl = generateOAuthRequestUrlAndSetParam(state);

        // 리다이렉트
        response.sendRedirect(authorizationUrl);
    }

    private String generateOAuthRequestUrlAndSetParam(String state) {
        return UriComponentsBuilder
                .fromUriString("https://nid.naver.com/oauth2.0/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", myInfoConfig.getClientId())
                .queryParam("redirect_uri", myInfoConfig.getRedirectUri())
                .queryParam("state", state)
                .toUriString();
    }

    @GetMapping("/login/naver/callback")
    public ResponseEntity<?> callback(
            @RequestParam("code") String code,
            @RequestParam("state") String state
    ) {
        LoginResponseDto response = oauthService.verifyUserByToken(code, state);
        return ResponseEntity.ok(response);
    }
}
