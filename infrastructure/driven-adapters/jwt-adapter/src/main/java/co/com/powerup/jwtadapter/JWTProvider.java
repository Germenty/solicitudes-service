package co.com.powerup.jwtadapter;

import co.com.powerup.jwtadapter.config.JwtProperties;
import co.com.powerup.model.jwt.gateways.JWTRepository;
import co.com.powerup.model.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JWTProvider implements JWTRepository {

    private final JwtProperties jwtProperties;


    @Override
    public Boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(getKey(jwtProperties.getSecretKey()))
                    .build()
                    .parseClaimsJws(token);

            Date expiration = claimsJws.getBody().getExpiration();
            return expiration.after(new Date());

        } catch (JwtException | IllegalArgumentException e) {
            // Simplemente devolvemos false sin logs
            return false;
        }
    }

    @Override
    public String extractUserEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    @Override
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey(jwtProperties.getSecretKey()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getKey(String secretKey) {
        byte[] secretBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(secretBytes);
    }
}
