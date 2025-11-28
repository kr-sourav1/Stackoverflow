package com.stackoverflow.beta.filter;

import com.stackoverflow.beta.model.User;
import com.stackoverflow.beta.model.dto.CustomUserDetails;
import com.stackoverflow.beta.repository.UserRepository;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Autowired
    private UserRepository userRepository;

    // Use a secret key from properties for better security
    @Value("${jwt.secret}")
    private String secretKey;

    private final long JWT_EXPIRATION = 1000 * 60 * 60; // 1 hour

    // Method to get the signing key
    private Key getSigningKey() {
        return new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }

    /**
     * Generate a JWT token for a given username.
     *
     * @return the generated JWT token
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        int userId = getUserIdFromAuthentication(authentication);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("name", user.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION)) // Set expiration
                .signWith(SignatureAlgorithm.HS256, getSigningKey()) // Use a key for signing
                .compact();
    }

    /**
     * Extract claims from a JWT token.
     *
     * @param token the JWT token
     * @return the claims extracted from the token
     */
    public Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            System.out.println("JWT Token has expired.");
            throw e; // Handle expired token scenario
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT Token.");
            throw e; // Handle malformed token scenario
        } catch (SignatureException e) {
            System.out.println("JWT signature does not match.");
            throw e; // Handle invalid signature scenario
        }
    }

    /**
     * Extract the username from a JWT token.
     *
     * @param token the JWT token
     * @return the username
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    //extract user name
//    public String extractName(String token) {
//        return extractClaims(token).get("name", String.class);
//    }


    /**
     * Check if a token has expired.
     *
     * @param token the JWT token
     * @return true if the token has expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    /**
     * Validate a token against the UserDetails object.
     *
     * @param token       the JWT token
     * @param userDetails the UserDetails object to validate against
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public int getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUserId();
        }
        throw new RuntimeException("EmailId is not valid!!");
    }

}