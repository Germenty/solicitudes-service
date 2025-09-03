package co.com.powerup.r2dbc.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table("estados.estado")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EstadoEntity {

    @Id
    @Column("id_estado")
    public Long idEstado;
    
    public String nombre;
    public String descripcion;

}
