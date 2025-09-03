package co.com.powerup.api;

import java.net.URI;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.powerup.api.dto.RegisterSolicitudRequest;
import co.com.powerup.model.estado.Estado;
import co.com.powerup.model.solicitud.Solicitud;
import co.com.powerup.model.tipoprestamo.TipoPrestamo;
import co.com.powerup.usecase.solicitud.SolicitudUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final SolicitudUseCase solicitudUseCase;

    // POST /api/v1/solicitud
    public Mono<ServerResponse> createSolicitud(ServerRequest request) {
        return request
                .bodyToMono(RegisterSolicitudRequest.class)

                // 1. Mapear DTO → Dominio
                .map((RegisterSolicitudRequest dto) -> Solicitud.builder()
                        .monto(dto.monto())
                        .plazo(dto.plazo())
                        .email(dto.email())
                        .estado(Estado.builder()
                                .idEstado(String.valueOf(dto.idEstado()))
                                .build())
                        .tipoPrestamo(TipoPrestamo.builder()
                                .idTipoPrestamo(String.valueOf(dto.idTipoPrestamo()))
                                .build())
                        .build())

                // 2. Ejecutar caso de uso
                .flatMap(solicitudUseCase::createSolicitud)

                // 3. Responder con 201 Created
                .flatMap(saved -> ServerResponse.created(
                        URI.create("/api/v1/solicitud/" + saved.getEmail()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(saved))

                // 4. Manejo de errores de negocio
                .onErrorResume(IllegalArgumentException.class, ex -> ServerResponse.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", ex.getMessage())));
    }
}