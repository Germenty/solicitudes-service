package co.com.powerup.r2dbc;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

import co.com.powerup.model.estado.Estado;
import co.com.powerup.model.estado.gateways.EstadoRepository;
import co.com.powerup.r2dbc.entity.EstadoEntity;
import co.com.powerup.r2dbc.helper.ReactiveAdapterOperations;
import co.com.powerup.r2dbc.repository.EstadoreactiveRepository;
import reactor.core.publisher.Mono;

@Repository
public class EstadoReactiveRepositoryAdapter extends
        ReactiveAdapterOperations<Estado, EstadoEntity, Long, EstadoreactiveRepository> implements EstadoRepository {

    protected EstadoReactiveRepositoryAdapter(EstadoreactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, estadoEntity -> Estado.builder()
                .idEstado(String.valueOf(estadoEntity.getIdEstado()))
                .nombre(estadoEntity.getNombre())
                .descripcion(estadoEntity.getDescripcion())
                .build());
    }

    @Override
    public Mono<Estado> findById(String id) {
        return repository.findById(Long.valueOf(id)).map(this::toEntity)
                .map(estadoEntity -> Estado.builder()
                        .idEstado(String.valueOf(estadoEntity.getIdEstado()))
                        .nombre(estadoEntity.getNombre())
                        .descripcion(estadoEntity.getDescripcion())
                        .build());
    }

}
