package co.com.powerup.usecase.solicitud.validation;

import co.com.powerup.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

public class SolicitudValidator {

    private SolicitudValidator() throws IllegalAccessException {
        throw new IllegalAccessException("Utility class");
    }

    public static Mono<Solicitud> validate(Solicitud solicitud){
        return Mono.just(solicitud);
    }



}
