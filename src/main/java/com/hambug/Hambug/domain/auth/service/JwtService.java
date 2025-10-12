package com.hambug.Hambug.domain.auth.service;

import com.hambug.Hambug.domain.auth.dto.JwtTokenDto;
import com.hambug.Hambug.domain.auth.entity.Token;
import com.hambug.Hambug.domain.auth.entity.TokenType;
import com.hambug.Hambug.domain.auth.repository.TokenRepository;
import com.hambug.Hambug.domain.oauth.service.Oauth2UserInfo;
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

import static com.hambug.Hambug.global.exception.ErrorCode.JWT_TOKEN_EXPIRED;
import static com.hambug.Hambug.global.exception.ErrorCode.JWT_TOKEN_INVALID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
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
        return tokenRepository.findByUserIdAndType(userDto.getUserId(), TokenType.REFRESH_TOKEN)
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

    public JwtTokenDto generateTokens(JwtPrincipal principal) {
        String access = generateAccessToken(principal);
        if (principal instanceof UserJwtPrincipalAdapter u) {
            String refresh = generateRefreshToken(u.getUserDto()).getToken();
            return JwtTokenDto.of(access, refresh);
        } else if (principal instanceof AdminJwtPrincipalAdapter a) {
            String refresh = getOrCreateAdminRefresh(a);
            return JwtTokenDto.of(access, refresh);
        }
        // Fallback: access only
        return JwtTokenDto.of(access, null);
    }

    public void logout(Long userId) {
        softDelete(userId);
        eventPublisher.publishEvent(new UserLogoutFcmEvent(userId));
    }

    @Transactional
    public void softDelete(Long userId) {
        tokenRepository.findAllByUserId(userId)
                .forEach(Token::markDeleted);
    }

    @Transactional
    public String reissueTokensFromRefresh(String refreshToken) {
        Claims claims = validateToken(refreshToken);
        if (!"refresh".equals(claims.get("type"))) {
            throw new JwtException(JWT_TOKEN_EXPIRED);
        }
        boolean tokenExpired = isTokenExpired(refreshToken);
        if (!tokenExpired) {
            Object actor = claims.get("actor");
            if ("admin".equals(actor)) {
                Long id = Long.parseLong(claims.getSubject());
                String role = claims.get("role") != null ? claims.get("role").toString() : null;
                JwtPrincipal principal = new SimplePrincipal(id, role);
                return generateAccessToken(principal);
            }
            // default user path for backward compatibility
            return generateAccessToken(UserDto.reissue(claims));
        }

        tokenRepository.deleteByToken(refreshToken);
        throw new JwtException(JWT_TOKEN_INVALID);
    }

    public String generateAccessToken(UserDto userDto) {
        return generateAccessToken(new UserJwtPrincipalAdapter(userDto));
    }

    public String generateAccessToken(JwtPrincipal principal) {
        Instant now = Instant.now();
        Instant expiry = now.plus(accessTokenExpiryMinutes, ChronoUnit.MINUTES);

        JwtBuilder builder = Jwts.builder()
                .setIssuer(issuer)
                .setSubject(String.valueOf(principal.getId()))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .claim("type", "access");

        if (principal.getRoleAsString() != null) {
            builder.claim("role", principal.getRoleAsString());
        }

        if (principal.getNickname() != null) {
            builder.claim("nickname", principal.getNickname());
        }
        if (principal.getPlatform() != null) {
            builder.claim("platform", principal.getPlatform());
        }

        return builder.signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public Token generateRefreshToken(UserDto userDto) {
        Result result = buildRefreshJwt(userDto);
        return tokenRepository.save(Token.of(result.jwtToken(), result.expiredAt(), userDto));
    }

    private String getOrCreateAdminRefresh(AdminJwtPrincipalAdapter principal) {
        Long adminId = principal.getId();
        return tokenRepository.findByAdminUserId(adminId)
                .map(existing -> {
                    if (isTokenExpired(existing.getToken())) {
                        Result result = buildRefreshJwt(principal);
                        existing.setExpiredAt(result.expiredAt());
                        existing.setToken(result.jwtToken());
                        return result.jwtToken();
                    }
                    return existing.getToken();
                })
                .orElseGet(() -> {
                    Result result = buildRefreshJwt(principal);
                    tokenRepository.save(Token.of(result.jwtToken(), result.expiredAt(), principal.getAdminUser()));
                    return result.jwtToken();
                });
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
        Claims claims = parseToken(token);
        if ("refresh".equals(claims.get("type"))) {
            log.info("엥에에");
            tokenRepository.findByToken(token).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 토큰입니다."));
        }
        return claims;
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

    @Transactional
    public void socialRefreshToken(Oauth2UserInfo userInfo, UserDto userDto) {
        tokenRepository.save(Token.socialOf(userInfo, userDto));

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
                .claim("actor", "user")
                .claim("nickname", userDto.getNickname())
                .claim("role", userDto.getRole())
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();

        LocalDateTime expiredAt = LocalDateTime.ofInstant(expiry, ZoneId.systemDefault());
        return new Result(jwtToken, expiredAt);
    }

    private Result buildRefreshJwt(AdminJwtPrincipalAdapter principal) {
        Instant now = Instant.now();
        Instant expiry = now.plus(refreshTokenExpiryDays, ChronoUnit.DAYS);

        String jwtToken = Jwts.builder()
                .setIssuer(issuer)
                .setSubject(String.valueOf(principal.getId()))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .claim("type", "refresh")
                .claim("actor", "admin")
                .claim("role", principal.getRoleAsString())
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();

        LocalDateTime expiredAt = LocalDateTime.ofInstant(expiry, ZoneId.systemDefault());
        return new Result(jwtToken, expiredAt);
    }

    public String getAppleRefreshToken(Long userId) {
        return tokenRepository.findByUserIdAndType(userId, TokenType.APPLE_REFRESH_TOKEN)
                .map(Token::getToken)
                .orElseThrow(() -> new EntityNotFoundException("토큰을 찾을수 없습니다."));
    }

    private record Result(String jwtToken, LocalDateTime expiredAt) {
    }

    // Lightweight principal for reissue without DB access
    private record SimplePrincipal(Long id, String role) implements JwtPrincipal {
        @Override
        public Long getId() {
            return id;
        }

        @Override
        public String getRoleAsString() {
            return role;
        }
    }
}
