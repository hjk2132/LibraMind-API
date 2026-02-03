package com.no_plan.library_api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;
    private final UserDetailsService userDetailsService;

    @PostConstruct
    public void init() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(Long userId, boolean isAdmin) {
        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + 60 * 60 * 1000);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("isAdmin", isAdmin)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Long userId) {
        long now = (new Date()).getTime();
        Date refreshTokenExpiresIn = new Date(now + 7 * 24 * 60 * 60 * 1000);

        return Jwts.builder()
                .setSubject(userId.toString())
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Long getExpiration(String acessToken) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(acessToken)
                .getBody()
                .getExpiration();
        long now = new Date().getTime();

        return (expiration.getTime() - now);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 JWT 토큰: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("잘못된 JWT 토큰: {}", e.getMessage());
        }

        return false;
    }

    public Long getUserId(String token) {
        return Long.parseLong(parseClaims(token).getSubject());
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}