package org.inmobiliaria.apiinmobiliaria.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Inmueble")
@Table(name = "inmueble")
public class Inmueble {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @NotBlank (message = "El título es obligatorio")
    @Size(max = 150)
    private String titulo;

    @Column
    @NotNull (message = "El precio es obligatorio")
    @Positive (message = "El precio tiene que ser positivo")
    private Float precio;

    @Column
    @NotNull(message ="Los metros son obligatorios")
    @Min(value = 1, message = "El valor tiene que ser mayor que 1")
    private Integer metros;

    @Column
    @NotNull (message = "Introduzca un dato válido")
    private Double latitud;

    @Column
    @NotNull (message = "Introduzca un dato válido")
    private Double longitud;

    @Column
    @NotNull(message = "Debe indicar si tiene ascensor")
    private Boolean ascensor;

    @Column(name = "fecha_publicacion")
    @NotNull(message = "La fecha es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaPublicacion;

    // Relación N:1 con Agencia
    @ManyToOne
    @JoinColumn(name = "agencia_id")
    private Agencia agencia;

    // Relación N:1 con Propietario
    @ManyToOne
    @JoinColumn(name = "propietario_id")
    private Propietario propietario;

    // Relación 1:N con Visitas
    @OneToMany(mappedBy = "inmueble")
    @JsonBackReference
    private List<Visita> visitas;
}
