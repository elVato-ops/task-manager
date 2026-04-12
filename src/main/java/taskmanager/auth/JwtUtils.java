package taskmanager.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import taskmanager.user.UserRole;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils
{
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(Long userId, String username, UserRole role)
    {
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("role", role.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token)
    {
        return getClaims(token).getSubject();
    }

    public Long extractUserId(String token)
    {
        return getClaims(token).get("userId", Long.class);
    }

    public UserRole extractUserRole(String token)
    {
        String role = getClaims(token).get("role", String.class);
        return UserRole.valueOf(role);
    }

    public boolean isTokenValid(String token)
    {
        try
        {
            getClaims(token);
            return true;
        }
        catch (JwtException e)
        {
            return false;
        }
    }

    private Claims getClaims(String token)
    {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey()
    {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}