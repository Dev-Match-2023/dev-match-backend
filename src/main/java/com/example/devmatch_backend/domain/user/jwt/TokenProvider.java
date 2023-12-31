package com.example.devmatch_backend.domain.user.jwt;

import com.example.devmatch_backend.domain.user.dto.TokenResponse;
import com.example.devmatch_backend.domain.user.entity.User;
import com.example.devmatch_backend.domain.user.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@Slf4j
public class TokenProvider implements
        InitializingBean {

    public static final String AUTHORITIES = "auth";
    private final String secret;
    private SecretKey key; // 대칭키 암호화에 특화된 클래스

    @Value("${jwt.secret}")
    public String stringSecret;
    private final long accessExpired;
    private final long refreshExpired;

    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    static final String BEARER_PREFIX = "Bearer ";

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access_expired-time}") long accessExpired,
            @Value("${jwt.refresh_expired-time}") long refreshExpired,
            AuthRepository authRepository, UserRepository userRepository) {
        this.secret = secret;
        this.accessExpired = accessExpired;
        this.refreshExpired = refreshExpired;
        this.authRepository = authRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] decoded = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(decoded);
    } // InitializingBean 인터페이스를 구현한 빈이 초기화될 때 호출되는 메소드

    public TokenResponse createFrom(Authentication authentication) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);

        Date accessExpiration = Date.from(issuedAt.plus(accessExpired, ChronoUnit.SECONDS));
        Date refreshExpiration = Date.from(issuedAt.plus(refreshExpired, ChronoUnit.SECONDS));

        String id = null;
        if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            id = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
        } else {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            if (oAuth2User.getAttributes().get("id") != null){
                id = oAuth2User.getAttributes().get("id").toString();
            }
            else {
                id = oAuth2User.getAttributes().get("sub").toString();
            }

        }

        User user = userRepository.findByOauthIdAndActiveIsTrue(id).get();

        var accessToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer("Catsama")
                .setIssuedAt(new Date())
                .setExpiration(accessExpiration)
                .claim("userId", user.getUserId())
                .signWith(key, SignatureAlgorithm.HS512)
                .claim(AUTHORITIES, authorities)
                .compact();

        var refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer("Catsama")
                .setIssuedAt(new Date())
                .setExpiration(refreshExpiration)
                .claim("userId", user.getUserId())
                .signWith(key, SignatureAlgorithm.HS512)
                .claim(AUTHORITIES, authorities)
                .compact();

        authRepository.saveToken(authentication.getName(), refreshToken);

        // 접속 시간 업데이트

//        user.updateLastAccessed(LocalDateTime.now());

        userRepository.save(user);


        return new TokenResponse(accessToken, refreshToken);
    }


    public Authentication resolveFrom(String token) {
        JwtParser jwtParser = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build();
        Claims claims = jwtParser
                .parseClaimsJws(token)
                .getBody();

        Long userId = claims.get("userId", Long.class);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException());
        String userEmail = user.getOauthId();
        Collection<SimpleGrantedAuthority> authorities = Stream.of(
                        String.valueOf(claims.get(AUTHORITIES)).split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());


        // credentials (비밀번호) - 인증 완료후 유출 가능성을 줄이기 위해 삭제
        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User(userEmail, "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validate(String token) {
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new RuntimeException();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException();
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException();
        } catch (IllegalArgumentException e) {
            throw new RuntimeException();
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            return token.substring(7);
        }
        throw  new RuntimeException();
    }

}