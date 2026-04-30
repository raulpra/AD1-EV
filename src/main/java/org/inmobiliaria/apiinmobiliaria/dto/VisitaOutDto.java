package org.inmobiliaria.apiinmobiliaria.dto;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisitaOutDto {
    private Long id;

    @NotNull(message = "La fecha y hora son obligatorias")
    private LocalDateTime fechaHora;

    @Size(max = 255)
    private String comentarios;

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "PENDIENTE|CONFIRMADA|CANCELADA", message = "Estado inválido")
    private String estado = "PENDIENTE";

    @Min(value = 0)
    @Max(value = 5)
    private Float valoracion;

    private Integer duracionEstimada;

    @NotNull(message = "Debe indicar si el recordatorio está activo")
    private Boolean recordatorioActivo;


    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "El ID del inmueble es obligatorio")
    private Long inmuebleId;
}