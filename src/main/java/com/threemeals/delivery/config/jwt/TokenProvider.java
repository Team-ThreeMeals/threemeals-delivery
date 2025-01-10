package com.threemeals.delivery.config.jwt;

import static com.threemeals.delivery.config.util.Token.*;

import java.time.Duration;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.threemeals.delivery.domain.auth.exception.ExpiredTokenException;
import com.threemeals.delivery.domain.auth.exception.InvalidTokenException;
import com.threemeals.delivery.domain.user.entity.Role;
import com.threemeals.delivery.domain.user.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {

	private final JwtProperties jwtProperties;

	public String generateToken(User user, String tokenType, Duration validPeriod) {

		Date expiration = new Date(new Date().getTime() + validPeriod.toMillis());

		return tokenType.equals(ACCESS_TOKEN_TYPE) ?
			makeAccessToken(expiration, user) : makeRefreshToken(expiration, user);
	}

	private String makeAccessToken(Date expiration, User user) {

		return Jwts.builder()
			.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
			.setIssuer(jwtProperties.getIssuer())
			.setIssuedAt(new Date())
			.setExpiration(expiration)
			.setSubject(String.valueOf(user.getId()))
			.claim("role", user.getRole()) // accessToken에만 Role 포함
			.claim("tokenType", ACCESS_TOKEN_TYPE)
			.signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
			.compact();
	}

	private String makeRefreshToken(Date expiration, User user) {

		return Jwts.builder()
			.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
			.setIssuer(jwtProperties.getIssuer())
			.setIssuedAt(new Date())
			.setExpiration(expiration)
			.setSubject(String.valueOf(user.getId()))
			.claim("tokenType", REFRESH_TOKEN_TYPE)
			.signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
			.compact();
	}

	public boolean isRefreshToken(String token) {
		Claims claims = getClaims(token);
		String tokenType = claims.get("tokenType", String.class);

		return tokenType.equals(REFRESH_TOKEN_TYPE);
	}

	public void validateToken(String rawToken) {
		try {
			String extractedToken = extractToken(rawToken);
			Jwts.parser()
				.setSigningKey(jwtProperties.getSecretKey()) // 비밀키로 복호화
				.parseClaimsJws(extractedToken);

		} catch (ExpiredJwtException e) {
			throw new ExpiredTokenException();
		} catch (Exception e) {
			throw new InvalidTokenException();
		}
	}

	public Long getUserId(String token) {
		try { // 개인적으로 이런 try-catch는 좀 별로인 듯...
			String subject = getClaims(token).getSubject();
			return Long.valueOf(subject);
		} catch (Exception e) {
			throw new InvalidTokenException();
		}
	}

	public Role getRole(String token) {
		Claims claims = getClaims(token);
		String role = claims.get("role", String.class);

		return Role.of(role);
	}

	private Claims getClaims(String token) {
		String extractedToken = extractToken(token);

		// 여기서 isValidToken()을 한 번 더 사용해야 하나... 크흠...
		return Jwts.parser()
			.setSigningKey(jwtProperties.getSecretKey())
			.parseClaimsJws(extractedToken)
			.getBody();
	}

	private String extractToken(String rawToken) { // "Bearer " 접두사 제거

		if (rawToken == null || rawToken.isBlank()) {
			throw new InvalidTokenException();
		}

		// Bearer 접두사가 없더라도 우선 허용.
		return rawToken.startsWith(BEARER_PREFIX) ?
			rawToken.substring(BEARER_PREFIX.length()) : rawToken;
	}

}
