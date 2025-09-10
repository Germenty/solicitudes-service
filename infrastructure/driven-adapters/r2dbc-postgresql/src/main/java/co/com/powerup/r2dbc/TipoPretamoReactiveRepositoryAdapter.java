package co.com.powerup.r2dbc;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

import co.com.powerup.model.tipoprestamo.TipoPrestamo;
import co.com.powerup.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.powerup.r2dbc.entity.TipoPrestamoEntity;
import co.com.powerup.r2dbc.helper.ReactiveAdapterOperations;
import co.com.powerup.r2dbc.repository.TipoPrestamoReactiveRepository;
import reactor.core.publisher.Mono;

@Repository
public class TipoPretamoReactiveRepositoryAdapter extends ReactiveAdapterOperations<TipoPrestamo, TipoPrestamoEntity, Long, TipoPrestamoReactiveRepository> implements TipoPrestamoRepository {

    protected TipoPretamoReactiveRepositoryAdapter(TipoPrestamoReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, tipoPrestamoEntity -> TipoPrestamo.builder()
                .idTipoPrestamo(String.valueOf(tipoPrestamoEntity.getIdTipoPrestamo()))
                .nombre(tipoPrestamoEntity.getNombre())
                .montoMinimo(tipoPrestamoEntity.getMontoMinimo())
                .montoMaximo(tipoPrestamoEntity.getMontoMaximo())
                .tasaInteres(tipoPrestamoEntity.getTasaInteres())
                .validacionAutomatica(tipoPrestamoEntity.getValidacionAutomatica())
                .build());
    }

    @Override   
    public Mono<TipoPrestamo> findById(String id) {
        return repository.findById(Long.valueOf(id)).map(this::toEntity)
                .map(tipoPrestamoEntity -> TipoPrestamo.builder()
                        .idTipoPrestamo(String.valueOf(tipoPrestamoEntity.getIdTipoPrestamo()))
                        .nombre(tipoPrestamoEntity.getNombre())
                        .montoMinimo(tipoPrestamoEntity.getMontoMinimo())
                        .montoMaximo(tipoPrestamoEntity.getMontoMaximo())
                        .tasaInteres(tipoPrestamoEntity.getTasaInteres())
                        .validacionAutomatica(tipoPrestamoEntity.getValidacionAutomatica())
                        .build());
    }


    
}
