package co.com.powerup.security;

import co.com.powerup.model.jwt.gateways.JWTRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JWTRepository jwtRepository;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        if (!jwtRepository.validateToken(token)) {
            return Mono.empty();
        }

        String email = jwtRepository.extractUserEmail(token);
        String role = jwtRepository.extractRole(token);

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        AbstractAuthenticationToken auth = new JwtAuthenticationToken(email, token, authorities);
        return Mono.just(auth);
    }
}
