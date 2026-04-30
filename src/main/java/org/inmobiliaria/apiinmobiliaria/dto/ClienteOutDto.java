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
public class ClienteOutDto {
    private long id;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El mail no tiene un formato válido")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20)
    private String telefono;

    @NotNull(message = "El presupuesto es obligatorio")
    @Positive(message = "El presupuesto debe ser positivo")
    private Float presupuestoMaximo;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 18, message = "Tiene que ser mayor de edad")
    private Integer edad;

    @NotNull(message = "Debe indicar si está suscrito")
    private Boolean suscrito;

}

