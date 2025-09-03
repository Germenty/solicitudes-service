package co.com.powerup.r2dbc.entity;

import java.math.BigDecimal;

import java.math.BigDecimal;

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
public class TipoPrestamoEntity {

    @Id
    @Column("id_tipo_prestamo")
    public Long idTipoPrestamo;
    
    public String nombre;
    public String descripcion;

    @Column("monto_minimo")
    public BigDecimal montoMinimo;

    @Column("monto_maximo")
    public BigDecimal montoMaximo;

    @Column("tasa_interes")
    public BigDecimal tasaInteres;

    @Column("validacion_automatica")
    public Boolean validacionAutomatica;
}
