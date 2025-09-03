package co.com.powerup.usecase.solicitud;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.powerup.model.estado.Estado;
import co.com.powerup.model.estado.gateways.EstadoRepository;
import co.com.powerup.model.solicitud.Solicitud;
import co.com.powerup.model.solicitud.gateways.SolicitudRepository;
import co.com.powerup.model.tipoprestamo.TipoPrestamo;
import co.com.powerup.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.powerup.usecase.solicitud.validation.SolicitudValidator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class SolicitudUseCaseTest {

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private TipoPrestamoRepository tipoPrestamoRepository;

    @Mock
    private EstadoRepository estadoRepository;

    @InjectMocks
    private SolicitudUseCase solicitudUseCase;

    private Solicitud solicitudBase;
    private TipoPrestamo tipoPrestamoCompleto;
    private Estado estadoCompleto;

    @BeforeEach
    void setUp() {
        tipoPrestamoCompleto = TipoPrestamo.builder()
                .idTipoPrestamo("1")
                .nombre("Consumo")
                .montoMinimo(BigDecimal.valueOf(100000))
                .montoMaximo(BigDecimal.valueOf(5000000))
                .tasaInteres(BigDecimal.valueOf(1.5))
                .validacionAutomatica(true)
                .build();

        estadoCompleto = Estado.builder()
                .idEstado("2")
                .nombre("Pendiente")
                .descripcion("Solicitud en espera de aprobación")
                .build();

        solicitudBase = Solicitud.builder()
                .monto(BigDecimal.valueOf(2500000))
                .plazo(36)
                .email("cliente@correo.com")
                .tipoPrestamo(TipoPrestamo.builder().idTipoPrestamo("1").build())
                .estado(Estado.builder().idEstado("2").build())
                .build();
    }

    // ✅ Caso exitoso
    @Test
    void whenCreateSolicitudWithValidData_thenReturnsSavedSolicitud() {
        given(tipoPrestamoRepository.findById("1"))
                .willReturn(Mono.just(tipoPrestamoCompleto));
        given(estadoRepository.findById("2"))
                .willReturn(Mono.just(estadoCompleto));
        given(solicitudRepository.save(any(Solicitud.class)))
                .willReturn(Mono.just(solicitudBase.toBuilder()
                        .tipoPrestamo(tipoPrestamoCompleto)
                        .estado(estadoCompleto)
                        .build()));

        StepVerifier.create(solicitudUseCase.createSolicitud(solicitudBase))
                .expectNextMatches(s -> s.getEmail().equals("cliente@correo.com")
                        && s.getTipoPrestamo().getNombre().equals("Consumo")
                        && s.getEstado().getNombre().equals("Pendiente"))
                .verifyComplete();
    }

    // ❌ Tipo de préstamo no existe
    @Test
    void whenTipoPrestamoNotFound_thenThrowsException() {
        given(tipoPrestamoRepository.findById("1"))
                .willReturn(Mono.empty());

        StepVerifier.create(solicitudUseCase.createSolicitud(solicitudBase))
                .expectErrorMatches(ex -> ex instanceof RuntimeException &&
                        ex.getMessage().equals("El tipo de prestamo no existe"))
                .verify();
    }

    // ❌ Estado no existe
    @Test
    void whenEstadoNotFound_thenThrowsException() {
        given(tipoPrestamoRepository.findById("1"))
                .willReturn(Mono.just(tipoPrestamoCompleto));
        given(estadoRepository.findById("2"))
                .willReturn(Mono.empty());

        StepVerifier.create(solicitudUseCase.createSolicitud(solicitudBase))
                .expectErrorMatches(ex -> ex instanceof RuntimeException &&
                        ex.getMessage().equals("El estado no existe"))
                .verify();
    }

    // ❌ Validación falla
    @Test
    void whenSolicitudFailsValidation_thenReturnsEmpty() {
        Solicitud solicitudInvalida = solicitudBase.toBuilder()
                .email("") // simulamos email inválido
                .build();

        try (MockedStatic<SolicitudValidator> mockedValidator = Mockito.mockStatic(SolicitudValidator.class)) {
            mockedValidator.when(() -> SolicitudValidator.validate(any(Solicitud.class)))
                    .thenReturn(Mono.empty());

            StepVerifier.create(solicitudUseCase.createSolicitud(solicitudInvalida))
                    .expectComplete()
                    .verify();
        }
    }

}