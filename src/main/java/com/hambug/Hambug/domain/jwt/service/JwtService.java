package com.hambug.Hambug.domain.jwt.service;

import com.hambug.Hambug.domain.jwt.dto.JwtTokenDto;
import com.hambug.Hambug.domain.jwt.entity.Token;
import com.hambug.Hambug.domain.jwt.repository.TokenRepository;
import com.hambug.Hambug.domain.user.dto.UserDto;
import io.jsonwebtoken.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final TokenRepository tokenRepository;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.access-token-expiry}")
    private long accessTokenExpiryMinutes;

    @Value("${jwt.refresh-token-expiry}")
    private long refreshTokenExpiryDays;

    public String getRefreshToken(Long userId) {
        return tokenRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("리프레쉬 토큰을 찾을수 없습니다."))
                .getToken();
    }

    public JwtTokenDto generateTokens(UserDto userDto) {
        return JwtTokenDto.of(generateAccessToken(userDto), generateRefreshToken(userDto));
    }

    public String generateAccessToken(UserDto userDto) {
        Instant now = Instant.now();
        Instant expiry = now.plus(accessTokenExpiryMinutes, ChronoUnit.MINUTES);

        JwtBuilder builder = Jwts.builder()
                .setIssuer(issuer)
                .setSubject(userDto.userIdStr())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .claim("email", userDto.getNickname())
                .claim("role", userDto.getRole())
                .claim("type", "access");

        return builder.signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String generateRefreshToken(UserDto userDto) {
        Instant now = Instant.now();
        Instant expiry = now.plus(refreshTokenExpiryDays, ChronoUnit.DAYS);

        String jwtToken = Jwts.builder()
                .setIssuer(issuer)
                .setSubject(String.valueOf(userDto.getUserId()))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .claim("type", "refresh")
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();

        LocalDateTime expiredAt = LocalDateTime.ofInstant(expiry, ZoneId.systemDefault());
        tokenRepository.save(Token.of(jwtToken, expiredAt, userDto));
        return jwtToken;
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationFromToken(token);
            return expiration.before(new Date());
        } catch (JwtException e) {
            throw new JwtException(e.getMessage());
        } catch (Exception e) {
            throw new ExpiredJwtException(null, null, "토큰 만료");
        }
    }

    public void validateToken(String token) {
        try {
            parseToken(token);
        } catch (Exception e) {
            log.warn("토큰 검증 실패: {}", e.getMessage());
            throw new JwtException("존재하지 않는 토큰 사용", e.getCause());
        }
    }

    public Date getExpirationFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.error("JWT 토큰 파싱 실패: {}", e.getMessage());
            throw new JwtException("유효하지 않은 JWT 토큰입니다.");
        }
    }
}
