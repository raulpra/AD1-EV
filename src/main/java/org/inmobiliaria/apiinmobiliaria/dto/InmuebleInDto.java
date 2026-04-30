package org.inmobiliaria.apiinmobiliaria.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InmuebleInDto {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150)
    private String titulo;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio tiene que ser positivo")
    private Float precio;

    @NotNull(message = "Los metros son obligatorios")
    @Min(value = 1, message = "El valor tiene que ser mayor que 1")
    private Integer metros;

    @NotNull(message = "La latitud es obligatoria")
    private Double latitud;

    @NotNull(message = "La longitud es obligatoria")
    private Double longitud;

    @NotNull(message = "Debe indicarse si tiene ascensor")
    private Boolean ascensor;

    @NotNull(message = "La fecha de publicación es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "La fecha no puede ser futura")
    private LocalDate fechaPublicacion;

    @NotNull(message = "El ID de la agencia es obligatorio")
    private Long agenciaId;

    @NotNull(message = "El ID del propietario es obligatorio")
    private Long propietarioId;
}