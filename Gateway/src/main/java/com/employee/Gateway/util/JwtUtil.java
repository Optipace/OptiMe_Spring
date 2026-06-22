package com.employee.Gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    public SecretKey getSignKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public boolean validateToken(final String token){
        try{
            Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch (Exception ex){
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
        return extractClaims(token).get("EmailId",String.class);
    }

    public String extractContact(String token){
        return extractClaims(token).get("Contact", String.class);
    }

    public String extractRole(String token) {
        return extractClaims(token).get("Role", String.class);
    }

    public String extractEmployeeId(String token){
        return extractClaims(token).get("EmployeeId", String.class);
    }

}
