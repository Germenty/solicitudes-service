package co.com.powerup.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationManager jwtAuthenticationManager;
    private final JwtSecurityContextRepository jwtSecurityContextRepository;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authenticationManager(jwtAuthenticationManager)
                .securityContextRepository(jwtSecurityContextRepository)
                .authorizeExchange(exchanges -> exchanges
                        // Endpoints públicos
                        .pathMatchers("/api/v1/login").permitAll()
                        .pathMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**")
                        .permitAll()

                        // Endpoints protegidos por rol
                        .pathMatchers("/api/v1/users").hasRole("ADMIN") // Crear usuario
                        .pathMatchers("/api/v1/users/**").hasRole("CLIENT") // Consultar por email

                        // Lo demás requiere autenticación
                        .anyExchange().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(unauthorizedHandler())
                        .accessDeniedHandler(forbiddenHandler()))
                .build();
    }

    @Bean
    public ServerAuthenticationEntryPoint unauthorizedHandler() {
        return ((exchange, ex) -> {
            var response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("code", "AUTH_010");
            body.put("error", "No autorizado");
            body.put("message", "No tiene credenciales válidas");

            byte[] bytes = writeJson(body);
            return response.writeWith(
                    Mono.just(response.bufferFactory().wrap(bytes)));
        });
    }

    @Bean
    public ServerAccessDeniedHandler forbiddenHandler() {
        return (exchange, denied) -> {
            var response = exchange.getResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("code", "AUTH_013");
            body.put("error", "Forbidden");
            body.put("message", "No tienes permisos para acceder a este recurso");

            byte[] bytes = writeJson(body);
            return response.writeWith(
                    Mono.just(response.bufferFactory().wrap(bytes)));
        };
    }

    private byte[] writeJson(Map<String, Object> body) {
        try {
            return new ObjectMapper().writeValueAsString(body).getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            return ("{\"error\":\"Serialization error\"}").getBytes(StandardCharsets.UTF_8);
        }
    }
}
