package co.com.powerup.model.jwt.gateways;

public interface JWTRepository {

    Boolean validateToken(String token);

    String extractUserEmail(String token);

    String extractRole(String token);

}
