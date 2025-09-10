package co.com.powerup.r2dbc;

import java.math.BigDecimal;
import java.util.List;

import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import co.com.powerup.model.estado.Estado;
import co.com.powerup.model.solicitud.Solicitud;
import co.com.powerup.model.solicitud.gateways.SolicitudRepository;
import co.com.powerup.model.tipoprestamo.TipoPrestamo;
import co.com.powerup.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.powerup.r2dbc.entity.SolicitudEntity;
import co.com.powerup.r2dbc.helper.ReactiveAdapterOperations;
import co.com.powerup.r2dbc.repository.SolicitudReactiveRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class SolicitudReactiveRepositoryAdapter
        extends ReactiveAdapterOperations<Solicitud, SolicitudEntity, Long, SolicitudReactiveRepository>
        implements SolicitudRepository {

    private static final Logger log = LoggerFactory.getLogger(SolicitudReactiveRepositoryAdapter.class);
    private final TipoPrestamoRepository tipoPrestamoRepository;

    protected SolicitudReactiveRepositoryAdapter(
            SolicitudReactiveRepository repository,
            ObjectMapper mapper,
            TipoPrestamoRepository tipoPrestamoRepository) {
        super(repository, mapper, SolicitudReactiveRepositoryAdapter::maptoDomain);
        this.tipoPrestamoRepository = tipoPrestamoRepository;
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

    @Override
    public Flux<Solicitud> findByEstados(List<String> estados) {
        List<Long> estadoIds = estados.stream()
                .map(this::mapNombreEstadoToId)
                .filter(id -> id != null)
                .toList();

        return repository.findByIdEstadoIn(estadoIds)
                .map(SolicitudReactiveRepositoryAdapter::maptoDomain);
    }

    @Override
    public Flux<Solicitud> findFiltradas(String tipoPrestamo, BigDecimal minMonto, BigDecimal maxMonto, int page,
            int size) {
        return repository.findAll()
                .map(SolicitudReactiveRepositoryAdapter::maptoDomain)
                .filter(s -> s.getEmail() != null && !s.getEmail().isBlank())
                .flatMap(s -> tipoPrestamoRepository.findById(s.getTipoPrestamo().getIdTipoPrestamo())
                        .map(tp -> s.toBuilder().tipoPrestamo(tp).build())
                        .switchIfEmpty(Mono.just(s)))
                .filter(s -> tipoPrestamo == null || (s.getTipoPrestamo().getNombre() != null &&
                        tipoPrestamo.equalsIgnoreCase(s.getTipoPrestamo().getNombre())))
                .filter(s -> minMonto == null || s.getMonto().compareTo(minMonto) >= 0)
                .filter(s -> maxMonto == null || s.getMonto().compareTo(maxMonto) <= 0)
                .skip((long) page * size)
                .take(size);
    }

    @Override
    public Mono<Long> countFiltradas(String tipoPrestamo, BigDecimal minMonto, BigDecimal maxMonto) {
        return repository.findAll()
                .map(SolicitudReactiveRepositoryAdapter::maptoDomain)
                .filter(s -> s.getEmail() != null && !s.getEmail().isBlank())
                .flatMap(s -> tipoPrestamoRepository.findById(s.getTipoPrestamo().getIdTipoPrestamo())
                        .map(tp -> s.toBuilder().tipoPrestamo(tp).build())
                        .onErrorResume(ex -> Mono.just(s)))
                .filter(s -> tipoPrestamo == null || tipoPrestamo.equalsIgnoreCase(s.getTipoPrestamo().getNombre()))
                .filter(s -> minMonto == null || s.getMonto().compareTo(minMonto) >= 0)
                .filter(s -> maxMonto == null || s.getMonto().compareTo(maxMonto) <= 0)
                .count();
    }

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

    private Long mapNombreEstadoToId(String nombre) {
        return switch (nombre) {
            case "PENDING" -> 1L;
            case "APPROVED" -> 2L;
            case "REJECTED" -> 3L;
            case "IN_PROGRESS" -> 4L;
            case "COMPLETED" -> 5L;
            case "CANCELLED" -> 6L;
            case "EXPIRED" -> 7L;
            default -> null;
        };
    }
}