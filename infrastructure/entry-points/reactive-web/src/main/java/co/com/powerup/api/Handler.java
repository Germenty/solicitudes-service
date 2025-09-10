package co.com.powerup.api;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.powerup.api.dto.RegisterSolicitudRequest;
import co.com.powerup.model.estado.Estado;
import co.com.powerup.model.solicitud.Solicitud;
import co.com.powerup.model.solicitud.SolicitudFilter;
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
        // 0. Extraer token de Authorization header
        String token = request.headers().firstHeader("Authorization");
        if (token == null || token.isBlank()) {
            return ServerResponse.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("error", "Authorization header is required"));
        }

        return request
                .bodyToMono(RegisterSolicitudRequest.class)

                // 1. Mapear DTO → Dominio
                .map(dto -> Solicitud.builder()
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

                // 2. Ejecutar caso de uso con el token
                .flatMap(solicitud -> solicitudUseCase.createSolicitud(solicitud, token))

                // 3. Responder con 201 Created
                .flatMap(saved -> ServerResponse.created(
                        URI.create("/api/v1/solicitud/" + saved.getEmail()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(saved))

                // 4. Manejo de errores de negocio
                .onErrorResume(IllegalArgumentException.class, ex -> ServerResponse.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", ex.getMessage())))
                .onErrorResume(RuntimeException.class, ex -> ServerResponse.status(500)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", ex.getMessage())));
    }

    // GET /api/v1/solicitud
    public Mono<ServerResponse> listarSolicitudesFiltradas(ServerRequest request) {
        String token = request.headers().firstHeader("Authorization");
        if (token == null || token.isBlank()) {
            return ServerResponse.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("error", "Authorization header is required"));
        }

        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        String estadoSolicitud = request.queryParam("estadoSolicitud").orElse(null);
        String tipoPrestamo = request.queryParam("tipoPrestamo").orElse(null);
        BigDecimal minMonto = request.queryParam("minMonto").map(BigDecimal::new).orElse(null);
        BigDecimal maxMonto = request.queryParam("maxMonto").map(BigDecimal::new).orElse(null);

        SolicitudFilter filter = new SolicitudFilter(estadoSolicitud, tipoPrestamo, minMonto, maxMonto);

        return solicitudUseCase.listarSolicitudesFiltradas(token, filter, page, size)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .onErrorResume(ex -> ServerResponse.status(403)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("error", ex.getMessage())));
    }
}
