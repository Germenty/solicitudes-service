package co.com.powerup.api;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.powerup.api.dto.RegisterSolicitudRequest;
import co.com.powerup.model.solicitud.Solicitud;
import co.com.powerup.model.solicitud.SolicitudRevisionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(path = "/api/v1/solicitud", method = RequestMethod.POST, beanClass = Handler.class, beanMethod = "createSolicitud", operation = @Operation(operationId = "createSolicitud", summary = "Crear una nueva solicitud", requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = RegisterSolicitudRequest.class))), responses = {
                    @ApiResponse(responseCode = "201", description = "Solicitud creada exitosamente", content = @Content(schema = @Schema(implementation = Solicitud.class))),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            })),
            @RouterOperation(path = "/api/v1/solicitud", method = RequestMethod.GET, beanClass = Handler.class, beanMethod = "listarSolicitudesFiltradas", operation = @Operation(operationId = "listarSolicitudesParaRevision", summary = "Listar solicitudes pendientes de revisión manual", responses = {
                    @ApiResponse(responseCode = "200", description = "Listado de solicitudes", content = @Content(schema = @Schema(implementation = SolicitudRevisionResponse.class))),
                    @ApiResponse(responseCode = "403", description = "No autorizado"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }))
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route()
                .POST("/api/v1/solicitud", handler::createSolicitud)
                .GET("/api/v1/solicitud", handler::listarSolicitudesFiltradas)
                .build();
    }
}