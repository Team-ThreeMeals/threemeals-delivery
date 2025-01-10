package com.threemeals.delivery.domain.oauth.service;

import com.threemeals.delivery.config.MyInfoConfig;
import com.threemeals.delivery.config.jwt.TokenProvider;
import com.threemeals.delivery.domain.auth.dto.request.LoginRequestDto;
import com.threemeals.delivery.domain.auth.dto.request.SignupRequestDto;
import com.threemeals.delivery.domain.auth.service.AuthService;
import com.threemeals.delivery.domain.oauth.dto.NaverOAuth2UserInfo;
import com.threemeals.delivery.domain.oauth.dto.NaverUserInfoDto;
import com.threemeals.delivery.domain.oauth.dto.OAuth2UserInfo;
import com.threemeals.delivery.domain.oauth.util.UserPrincipal;
import com.threemeals.delivery.domain.user.entity.Role;
import com.threemeals.delivery.domain.user.entity.User;
import com.threemeals.delivery.domain.user.repository.UserRepository;
import com.threemeals.delivery.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

import static com.threemeals.delivery.config.util.Token.ACCESS_TOKEN_DURATION;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthUserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final MyInfoConfig myInfoConfig;
    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final AuthService authService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (Exception e) {
            throw new OAuth2AuthenticationException("Failed to process OAuth2 user");
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        OAuth2UserInfo oauth2UserInfo = getOAuth2UserInfo(userRequest, oauth2User);

        Optional<User> userOptional = userRepository.findByEmail(oauth2UserInfo.getEmail());
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // address는 기존 값 유지하거나, 필요한 경우 OAuth 정보에서 가져온 값으로 설정
            user.update(oauth2UserInfo.getName());
            user = userRepository.save(user);
        } else {
            user = createUser(oauth2UserInfo);
        }

        return UserPrincipal.create(user, oauth2User.getAttributes());
    }

    private OAuth2UserInfo getOAuth2UserInfo(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        Map<String, Object> attributes = oauth2User.getAttributes();
        if ("naver".equals(registrationId)) {
            return new NaverOAuth2UserInfo((Map<String, Object>) attributes.get("response"));
        }

        throw new OAuth2AuthenticationException("Unsupported Provider");
    }

    private User createUser(OAuth2UserInfo oauth2UserInfo) {
        return User.builder()
                .username(oauth2UserInfo.getName())
                .email(oauth2UserInfo.getEmail())
                .password("") // OAuth 로그인의 경우 비밀번호 불필요
                .role(Role.USER)
                .address("") // 초기 주소는 빈 문자열 또는 기본값 설정
                .build();
    }
    public String verifyUserByToken(String code, String state){
        RestTemplate restTemplate = new RestTemplate();
        String accessToken = getAccessToken(code, state,restTemplate);
        if (accessToken == null) return null;
        NaverUserInfoDto userInfo = getUserInfo(accessToken,restTemplate);
        String userEmail = userInfo.getEmail();
        if (userEmail == null) return null;
        // DB에서 사용자 확인
        Optional<User> userOptional = userRepository.findByEmail(userEmail);//
        if (userOptional.isEmpty()) {
            authService.createUser(new SignupRequestDto(userEmail, userInfo.getName(), "", userInfo.getProfileImage(), ""));
            authService.authenticate(new LoginRequestDto(userEmail,""));
        }
        User user = userRepository.findByEmail(userEmail).get();//
        String token = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        return token;
    }
    private String getAccessToken(String code, String state,RestTemplate restTemplate) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", myInfoConfig.getClientId());
        params.add("client_secret", myInfoConfig.getClientSecret());
        params.add("code", code);
        params.add("state", state);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://nid.naver.com/oauth2.0/token", request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return (String) response.getBody().get("access_token");
        }

        return null;
    }
    private NaverUserInfoDto getUserInfo(String accessToken, RestTemplate restTemplate) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me", HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseData = (Map<String, Object>) response.getBody().get("response");

            String email = (String) responseData.get("email");
            String name = (String) responseData.get("name");
            String profileImage = (String) responseData.get("profile_image");

            return new NaverUserInfoDto(email, name, profileImage);
        }

        return null; // 실패 시 null 반환
    }
}
