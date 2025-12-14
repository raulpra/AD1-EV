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
public class AgenciaInDto {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 200)
    private String direccion;

    @NotNull(message = "La facturación es obligatoria")
    @Min(value=0, message="la facturación debe ser positiva")
    private Float facturacionAnual;

    @NotNull(message = "El código postal es obligatorio")
    private Integer codigoPostal;

    @NotNull(message = "Debes indicar si abre los sábados")
    private Boolean abiertoSabados;

    @NotNull(message = "La fecha de fundación es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Past(message = "La fecha de fundación debe ser pasada")
    private LocalDate fechaFundacion;

}
