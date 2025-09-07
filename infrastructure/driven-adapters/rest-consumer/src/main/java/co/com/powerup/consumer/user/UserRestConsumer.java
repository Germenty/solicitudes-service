package co.com.powerup.consumer.user;

import co.com.powerup.model.user.User;
import co.com.powerup.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserRestConsumer implements UserRepository {

    private final WebClient webClient;

    @Override
    public Mono<User> findByEmail(String email, String token) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/user/{email}")
                        .build(email))
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(),
                        response -> Mono.error(new RuntimeException("User not found with email: " + email))
                )
                .onStatus(
                        status -> status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Server error while fetching user: " + email))
                )
                .bodyToMono(User.class);
    }
}
