package com.threemeals.delivery.config.jwt;

import com.threemeals.delivery.domain.auth.exception.ExpiredTokenException;
import com.threemeals.delivery.domain.auth.exception.InvalidTokenException;
import com.threemeals.delivery.domain.user.entity.Role;
import com.threemeals.delivery.domain.user.entity.User;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

import static com.threemeals.delivery.config.util.Token.BEARER_PREFIX;


@Service
@RequiredArgsConstructor
public class TokenProvider {

    private final JwtProperties jwtProperties;

    public String generateToken(User user, Duration validPeriod) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + validPeriod.toMillis()), user);
    }

    private String makeToken(Date expiration, User user) {
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .setSubject(user.getEmail()) // 토큰에 email 정보를 노출하는 건 좀 위험하지 않나... 크흠...
                .claim("id", user.getId())
                .claim("role", user.getRole())
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
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
        Claims claims = getClaims(token);

        return claims.get("id", Long.class);
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
