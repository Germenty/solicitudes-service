package co.com.powerup.usecase.solicitud;

import co.com.powerup.model.solicitud.Solicitud;
import co.com.powerup.model.solicitud.gateways.SolicitudRepository;
import co.com.powerup.usecase.solicitud.validation.SolicitudValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SolicitudUseCase {

    private final SolicitudRepository solicitudRepository;

    public Mono<Solicitud> createSolicitud(Solicitud solicitud){
        return SolicitudValidator.validate(solicitud)
                .switchIfEmpty(solicitudRepository.save(solicitud));
    }



}
