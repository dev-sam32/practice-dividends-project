package com.dayone.security;

import ch.qos.logback.classic.joran.action.RootLoggerAction;
import com.dayone.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


// TODO : Blogging Subject
// Jwt가 Deprecate 된 내용이 상당 수 있었다

@Component
@RequiredArgsConstructor
public class TokenProvider {
    private static final String KEY_ROLES = "roles";
    private static final long TOKEN_EXPIRED_TIME = 1000 * 60 * 60; // 1h

    private final MemberService memberService;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    // Jwt Update
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = this.memberService.loadUserByUsername(this.getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * Jwt 토큰 발급
     * @param username
     * @param roles
     * @return
     */
    public String generateToken(String username, List<String> roles) {
//        Jwts.claims().setSubject(username);
//        Claims claims = Jwts.claims().subject(username).build();
//        claims.put(KEY_ROLES, roles);
        Claims claims = Jwts.claims().subject(username).add(KEY_ROLES, roles).build();

        var now = new Date();
        var expiredTime = new Date(now.getTime() + TOKEN_EXPIRED_TIME);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)              // 토큰 생성 시간
                .expiration(expiredTime)    // 토큰 만료 시간
                .signWith(this.getSigningKey()) // 암호화 알고리즘, 비밀키
                .compact();
    }

//    https://www.youtube.com/watch?v=EjrlN_OQVDQ&ab_channel=PhegonDev
//
//    /**
//     * Jwt 토큰 발급(UserDetails 사용)
//     * @param userDetails
//     * @return
//     */
//    public String generateToken(UserDetails userDetails) {
//        return Jwts.builder()
//                .subject(userDetails.getUsername())
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRED_TIME))
//                .signWith(this.getSigningKey())
//                .compact();
//    }


//    /**
//     * Jwt 리프레시 토큰 발급(UserDetails 사용)
//     *
//     * @param userDetails
//     * @return
//     */
//    public String generateRefreshToken(HashMap<String, Object> claims, UserDetails userDetails) {
//        return Jwts.builder()
//                .claims(claims)
//                .subject(userDetails.getUsername())
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRED_TIME))
//                .signWith(this.getSigningKey())
//                .compact();
//    }

    public String getUsername(String token) {
        return this.parseClaims(token).getSubject();
    }

    // 토큰이 비어있는지, 토큰 만료 시간을 넘지 않았는지 확인
    public boolean validateToken(String token) {
        // str != null && !str.isBlank();
        if(!StringUtils.hasText(token)) return false;

        var claims = this.parseClaims(token);
        return !claims.getExpiration().before(new Date());
    }

    // Updated Jwt
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(this.getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            // TODO : 추후 예외처리 추가
            return e.getClaims();
        }
    }

}
