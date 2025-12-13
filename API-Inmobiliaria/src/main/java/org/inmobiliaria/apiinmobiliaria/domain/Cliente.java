package org.inmobiliaria.apiinmobiliaria.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Size(max = 20)
    private String telefono;

    @Column(name = "presupuesto_maximo")
    private Float presupuestoMaximo;

    @Column
    @Min(value = 18, message = "Tiene que ser mayor de edad")
    private Integer edad;

    @Column(name = "fecha_alta")
    private LocalDate fechaAlta;

    @Column
    private Boolean suscrito;

    // Relación 1:N con Visitas
    @OneToMany(mappedBy = "cliente")
    @ToString.Exclude
    private List<Visita> visitas;
}
