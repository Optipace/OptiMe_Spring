//package com.employee.EmployeeProfileService.util;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.security.Key;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class JwtUtil {
//
//    @Value("${jwt.expirationMs}")
//    private Long expirationMs;// 30 minutes
//
//    @Value("${jwt.secret}")
//    private String secretKey;
//
//    public String generateToken(String username, String contact,String EmailId, String EmployeeId){
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("Contact", contact);
//        claims.put("EmailId", EmailId);
//        claims.put("EmployeeId", EmployeeId);
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(username)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
//                .signWith(getKey())
//                .compact();
//    }
//
//    private Key getKey() {
//        return Keys.hmacShaKeyFor(secretKey.getBytes());
//    }
//
//    public Claims extractClaims(String token){
//        return Jwts.parserBuilder()
//                .setSigningKey(getKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    public String extractUsername(String token){
//        return extractClaims(token).getSubject();
//    }
//
//    public String extractContact(String token){
//        return extractClaims(token).get("Contact", String.class);
//    }
//
//    public String extractEmailId(String token){
//        return extractClaims(token).get("EmailId", String.class);
//    }
//
//    public String extractEmployeeId(String token){
//        return extractClaims(token).get("EmployeeId",String.class);
//    }
//
//    public boolean validateToken(String token){
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(getKey())
//                    .build()
//                    .parseClaimsJws(token);
//            return true;
//        }catch(Exception e) {
//            return false;
//        }
//    }
//
//    public boolean isTokenExpired(String token) {
//        return extractClaims(token).getExpiration().before(new Date());
//    }
//}
