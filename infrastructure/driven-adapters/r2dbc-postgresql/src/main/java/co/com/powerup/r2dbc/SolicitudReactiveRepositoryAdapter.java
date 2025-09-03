package co.com.powerup.r2dbc;

import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import co.com.powerup.model.estado.Estado;
import co.com.powerup.model.solicitud.Solicitud;
import co.com.powerup.model.solicitud.gateways.SolicitudRepository;
import co.com.powerup.model.tipoprestamo.TipoPrestamo;
import co.com.powerup.r2dbc.entity.SolicitudEntity;
import co.com.powerup.r2dbc.helper.ReactiveAdapterOperations;
import co.com.powerup.r2dbc.repository.SolicitudReactiveRepository;
import reactor.core.publisher.Mono;

@Repository
public class SolicitudReactiveRepositoryAdapter
        extends ReactiveAdapterOperations<Solicitud, SolicitudEntity, Long, SolicitudReactiveRepository>
        implements SolicitudRepository {

    private static final Logger log = LoggerFactory.getLogger(SolicitudReactiveRepositoryAdapter.class);

    protected SolicitudReactiveRepositoryAdapter(SolicitudReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> maptoDomain(entity));
        log.info("Initializing SolicitudReactiveRepositoryAdapter");

    }

    @Override
    public Mono<Solicitud> save(Solicitud solicitud) {
        log.info("Persistiendo solicitud: email={}, monto={}, plazo={}, estado={}, tipoPrestamo={}",
                solicitud.getEmail(),
                solicitud.getMonto(),
                solicitud.getPlazo(),
                solicitud.getEstado() != null ? solicitud.getEstado().getIdEstado() : "null",
                solicitud.getTipoPrestamo() != null ? solicitud.getTipoPrestamo().getIdTipoPrestamo() : "null");

        SolicitudEntity solicitudEntity = maptoEntity(solicitud);
        return repository.save(solicitudEntity)
                .map(savedEntity -> {
                    Solicitud savedSolicitud = maptoDomain(savedEntity);

                    savedSolicitud.setTipoPrestamo(solicitud.getTipoPrestamo());
                    savedSolicitud.setEstado(solicitud.getEstado());
                    return savedSolicitud;
                });
    }

    // convertir de entidad a dominio
    private static Solicitud maptoDomain(SolicitudEntity solicitudEntity) {
        Estado estado = Estado.builder()
                .idEstado(String.valueOf(solicitudEntity.getIdEstado()))
                .build();

        TipoPrestamo tipoprestamo = TipoPrestamo.builder()
                .idTipoPrestamo(String.valueOf(solicitudEntity.getIdTipoPrestamo()))
                .build();

        return Solicitud.builder()
                .monto(solicitudEntity.getMonto())
                .plazo(solicitudEntity.getPlazo())
                .email(solicitudEntity.getEmail())
                .estado(estado)
                .tipoPrestamo(tipoprestamo)
                .build();
    }

    // convertir de dominio a entidad
    private static SolicitudEntity maptoEntity(Solicitud solicitud) {
        SolicitudEntity solicitudEntity = new SolicitudEntity();
        solicitudEntity.setMonto(solicitud.getMonto());
        solicitudEntity.setPlazo(solicitud.getPlazo());
        solicitudEntity.setEmail(solicitud.getEmail());
        if (solicitud.getEstado() != null) {
            solicitudEntity.setIdEstado(Long.valueOf(solicitud.getEstado().getIdEstado()));
        }
        if (solicitud.getTipoPrestamo() != null) {
            solicitudEntity.setIdTipoPrestamo(Long.valueOf(solicitud.getTipoPrestamo().getIdTipoPrestamo()));

        }
        return solicitudEntity;
    }

}
