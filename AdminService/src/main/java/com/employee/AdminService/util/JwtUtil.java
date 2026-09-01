package com.employee.AdminService.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value(("${jwt.expirationMs}"))
    private long expirationMs;

//    private long expirationMs = 60000;

    public SecretKey getSignKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public boolean validateToken(final String token){
        try{
            Claims claims = Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date issuedAt = claims.getIssuedAt();
            if(issuedAt == null){
                log.warn("JWT token is missing the IssuedAt claims");
                return false;
            }

            long expirationTimeMs = (issuedAt.getTime() + expirationMs);
            if(System.currentTimeMillis() > expirationTimeMs){
                log.info("JWT token validation failed : Token expired");
                return false;
            }
            return true;
        }catch (Exception ex){
            log.error("JWT Token signature verification or structural parsing failed", ex);
            return false;
        }
    }

    public Claims extractClaims(String token){
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token){
        return extractClaims(token).getSubject();
    }

    public String extractEmailId(String token){
        return extractClaims(token).get("emailId",String.class);
    }

    public String extractUserId(String token){
        return extractClaims(token).get("userId", String.class);
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public String extractEmployeeId(String token){
        return extractClaims(token).get("employeeId", String.class);
    }

    public String extractId(String token){
        return extractClaims(token).get("id", String.class);
    }

}
