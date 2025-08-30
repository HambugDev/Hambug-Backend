package com.hambug.Hambug.domain.auth.service;

import com.hambug.Hambug.domain.auth.dto.JwtTokenDto;
import com.hambug.Hambug.domain.auth.entity.Token;
import com.hambug.Hambug.domain.auth.repository.TokenRepository;
import com.hambug.Hambug.domain.user.dto.UserDto;
import com.hambug.Hambug.global.event.UserLogoutFcmEvent;
import com.hambug.Hambug.global.exception.custom.JwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static com.hambug.Hambug.global.response.ErrorCode.JWT_TOKEN_EXPIRED;
import static com.hambug.Hambug.global.response.ErrorCode.JWT_TOKEN_INVALID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final TokenRepository tokenRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.access-token-expiry}")
    private long accessTokenExpiryMinutes;

    @Value("${jwt.refresh-token-expiry}")
    private long refreshTokenExpiryDays;

    @Transactional
    public String getRefreshToken(UserDto userDto) {
        return tokenRepository.findByUserId(userDto.getUserId())
                .map(existing -> {
                    if (isTokenExpired(existing.getToken())) {
                        Result result = buildRefreshJwt(userDto);
                        existing.setExpiredAt(result.expiredAt());
                        existing.setToken(result.jwtToken());
                        return result.jwtToken();
                    }
                    return existing.getToken();
                })
                .orElseGet(() -> generateRefreshToken(userDto).getToken());
    }

    public JwtTokenDto generateTokens(UserDto userDto) {
        return JwtTokenDto.of(generateAccessToken(userDto), generateRefreshToken(userDto).getToken());
    }

    public void logout(Long userId) {
        Token token = tokenRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException("토큰을 찾을수 없습니다."));
        tokenRepository.delete(token);
        eventPublisher.publishEvent(new UserLogoutFcmEvent(userId));
    }

    @Transactional
    public String reissueTokensFromRefresh(String refreshToken) {
        Claims claims = validateToken(refreshToken);
        if (!claims.get("type").equals("refresh")) {
            throw new JwtException(JWT_TOKEN_EXPIRED);
        }
        boolean tokenExpired = isTokenExpired(refreshToken);
        if (!tokenExpired) {
            return generateAccessToken(UserDto.reissue(claims));
        }

        tokenRepository.deleteByToken(refreshToken);
        throw new JwtException(JWT_TOKEN_INVALID);
    }

    public String generateAccessToken(UserDto userDto) {
        Instant now = Instant.now();
        Instant expiry = now.plus(accessTokenExpiryMinutes, ChronoUnit.MINUTES);

        JwtBuilder builder = Jwts.builder()
                .setIssuer(issuer)
                .setSubject(userDto.userIdStr())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .claim("nickname", userDto.getNickname())
                .claim("role", userDto.getRole())
                .claim("type", "access");

        return builder.signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public Token generateRefreshToken(UserDto userDto) {
        Result result = buildRefreshJwt(userDto);
        return tokenRepository.save(Token.of(result.jwtToken(), result.expiredAt(), userDto));
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationFromToken(token);
            return expiration.before(new Date());
        } catch (com.hambug.Hambug.global.exception.custom.JwtException | io.jsonwebtoken.JwtException e) {
            return true;
        }
    }

    public Claims validateToken(String token) {
        try {
            return parseToken(token);
        } catch (com.hambug.Hambug.global.exception.custom.JwtException e) {
            log.warn("토큰 검증 실패: {}", e.getMessage());
            throw e;
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
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("JWT 토큰 만료: {}", e.getMessage());
            throw new com.hambug.Hambug.global.exception.custom.JwtException(JWT_TOKEN_EXPIRED);
        } catch (io.jsonwebtoken.JwtException e) {
            log.error("JWT 토큰 파싱 실패: {}", e.getMessage());
            throw new com.hambug.Hambug.global.exception.custom.JwtException(JWT_TOKEN_INVALID);
        }
    }

    private Result buildRefreshJwt(UserDto userDto) {
        Instant now = Instant.now();
        Instant expiry = now.plus(refreshTokenExpiryDays, ChronoUnit.DAYS);

        String jwtToken = Jwts.builder()
                .setIssuer(issuer)
                .setSubject(String.valueOf(userDto.getUserId()))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .claim("type", "refresh")
                .claim("nickname", userDto.getNickname())
                .claim("role", userDto.getRole())
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();

        LocalDateTime expiredAt = LocalDateTime.ofInstant(expiry, ZoneId.systemDefault());
        return new Result(jwtToken, expiredAt);
    }

    private record Result(String jwtToken, LocalDateTime expiredAt) {
    }
}
