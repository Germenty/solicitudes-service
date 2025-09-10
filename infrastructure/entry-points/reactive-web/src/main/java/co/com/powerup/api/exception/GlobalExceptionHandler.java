package co.com.powerup.api.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Order(-2)
public class GlobalExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);

        String code;
        String message;
        var requestPath = exchange.getRequest().getPath().value();
        HttpStatus status;

        if (ex instanceof ApiException apiEx) {
            status = apiEx.getStatus();
            code = apiEx.getErrorCode();
            message = apiEx.getMessage();

        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            code = "APP_500";
            message = "Error interno del servidor";
        }

        ErrorResponse errResp = new ErrorResponse(
                code,
                message,
                requestPath,
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errResp);
            return exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse()
                            .bufferFactory()
                            .wrap(bytes)));
        } catch (Exception writeErr) {
            log.error("Error writing error response", writeErr);
            return Mono.error(writeErr);
        }
    }
}