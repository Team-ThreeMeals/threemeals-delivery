package com.threemeals.delivery.domain.oauth.service;

import com.threemeals.delivery.config.MyInfoConfig;
import com.threemeals.delivery.config.jwt.TokenProvider;
import com.threemeals.delivery.domain.auth.dto.request.LoginRequestDto;
import com.threemeals.delivery.domain.auth.dto.request.SignupRequestDto;
import com.threemeals.delivery.domain.auth.service.AuthService;
import com.threemeals.delivery.domain.oauth.dto.NaverUserInfoDto;
import com.threemeals.delivery.domain.user.entity.User;
import com.threemeals.delivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static com.threemeals.delivery.config.util.Token.ACCESS_TOKEN_DURATION;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthUserService extends DefaultOAuth2UserService {
    private static final String NAVER_TOKEN_URL = "https://nid.naver.com/oauth2.0/token";
    private static final String NAVER_USER_INFO_URL = "https://openapi.naver.com/v1/nid/me";

    private final UserRepository userRepository;
    private final MyInfoConfig myInfoConfig;
    private final TokenProvider tokenProvider;
    private final AuthService authService;

    public String verifyUserByToken(String code, String state) {
        String accessToken = requestAccessToken(code, state);
        if (accessToken == null) {
            log.error("Failed to obtain access token");
            return null;
        }

        NaverUserInfoDto userInfo = requestUserInfo(accessToken);
        if (!userInfo.isValid()) {
            log.error("Invalid user info received");
            return null;
        }

        User user = findUserOrSignUp(userInfo);
        return tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
    }

    private String requestAccessToken(String code, String state) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> request = createTokenRequest(code, state);
            ResponseEntity<Map> response = restTemplate.postForEntity(NAVER_TOKEN_URL, request, Map.class);

            return extractAccessToken(response);
        } catch (RestClientException e) {
            log.error("Failed to request access token", e);
            return null;
        }
    }

    private HttpEntity<MultiValueMap<String, String>> createTokenRequest(String code, String state) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", myInfoConfig.getClientId());
        params.add("client_secret", myInfoConfig.getClientSecret());
        params.add("code", code);
        params.add("state", state);

        return new HttpEntity<>(params, headers);
    }

    private String extractAccessToken(ResponseEntity<Map> response) {
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            return null;
        }
        return (String) response.getBody().get("access_token");
    }

    private NaverUserInfoDto requestUserInfo(String accessToken) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Void> request = createUserInfoRequest(accessToken);
            ResponseEntity<Map> response = restTemplate.exchange(
                    NAVER_USER_INFO_URL,
                    HttpMethod.GET,
                    request,
                    Map.class
            );

            return extractUserInfo(response);
        } catch (RestClientException e) {
            log.error("Failed to request user info", e);
            return NaverUserInfoDto.empty();
        }
    }

    private HttpEntity<Void> createUserInfoRequest(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        return new HttpEntity<>(headers);
    }

    private NaverUserInfoDto extractUserInfo(ResponseEntity<Map> response) {
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            return NaverUserInfoDto.empty();
        }

        Map<String, Object> responseData = (Map<String, Object>) response.getBody().get("response");
        return NaverUserInfoDto.builder()
                .email((String) responseData.get("email"))
                .name((String) responseData.get("name"))
                .profileImage((String) responseData.get("profile_image"))
                .build();
    }

    private User findUserOrSignUp(NaverUserInfoDto userInfo) {
        return userRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> createNewUser(userInfo));
    }
    private User createNewUser(NaverUserInfoDto userInfo) {
        SignupRequestDto signupRequest = new SignupRequestDto(userInfo.getEmail(), userInfo.getName(), "", userInfo.getProfileImage(), "") ;
        authService.createUser(signupRequest);
        authService.authenticate(new LoginRequestDto(userInfo.getEmail(), ""));

        return userRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() -> new RuntimeException("Failed to create user"));
    }
}
