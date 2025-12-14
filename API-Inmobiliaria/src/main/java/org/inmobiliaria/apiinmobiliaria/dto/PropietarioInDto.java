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
public class PropietarioInDto {

    @NotBlank(message = "El DNI es obligatorio")
    @Size(max = 20, message = "El DNI no puede superar los 20 caracteres")
    private String dni;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 15)
    private String telefono;

    @NotNull(message = "La comisión es obligatoria")
    @Min(value = 0, message = "La comisión no puede ser negativa")
    private Float comision;

    @NotNull(message = "Debe indicarse si es empresa o particular")
    private Boolean esEmpresa;

    @NotNull(message = "La fecha de alta es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaAlta;
}
