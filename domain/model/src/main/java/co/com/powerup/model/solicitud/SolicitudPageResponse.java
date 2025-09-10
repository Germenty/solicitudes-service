package co.com.powerup.model.solicitud;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudPageResponse {
    private int page;
    private int size;
    private long total;
    private List<SolicitudRevisionResponse> items;
}