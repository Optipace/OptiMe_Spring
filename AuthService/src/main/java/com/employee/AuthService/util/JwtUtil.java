package com.employee.AuthService.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.expirationMs}")
    private Long expirationMs;// 30 minutes

    @Value("${jwt.secret}")
    private String secretKey;

    public String generateToken(String username, String contact,String EmailId, String EmployeeId, String role){
        Map<String, Object> claims = new HashMap<>();
        claims.put("Contact", contact);
        claims.put("EmailId", EmailId);
        claims.put("EmployeeId", EmployeeId);
        claims.put("Role",role);
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public Claims extractClaims(String token){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token){
        return extractClaims(token).getSubject();
    }

    public String extractContact(String token){
        return extractClaims(token).get("Contact", String.class);
    }

    public String extractEmailId(String token){
        return extractClaims(token).get("EmailId", String.class);
    }

    public String extractEmployeeId(String token){
        return extractClaims(token).get("EmployeeId",String.class);
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch(Exception e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}
