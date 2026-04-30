package org.inmobiliaria.apiinmobiliaria.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Cliente")
@Table

public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @NotBlank (message = "El campo no puede estar vacío")
    @Email (message = "El mail no tiene un formato válido")
    @Size(max = 100)
    private String email;

    @Column
    @NotBlank(message = "El campo no puede estar vacío")
    private String password;

    @Column
    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20)
    private String telefono;

    @Column(name = "presupuesto_maximo")
    @NotNull(message = "El presupuesto es obligatorio")
    private Float presupuestoMaximo;

    @Column
    @NotNull(message = "La edad es obligatoria")
    @Min(value = 18, message = "Tiene que ser mayor de edad")
    private Integer edad;

    @Column(name = "fecha_alta")
    @NotNull(message = "La fecha de alta es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaAlta;

    @Column
    @NotNull(message = "El estado de suscripción es obligatorio")
    private Boolean suscrito;

    // Relación 1:N con Visitas
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Visita> visitas;
}
