package org.inmobiliaria.apiinmobiliaria.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Visita")
@Table
public class Visita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "fecha_hora")
    @NotNull (message = "Debe seleccionar una fecha")
    private LocalDateTime fechaHora;

    @Column
    @Size(max = 255)
    private String comentarios;

    @Column
    @Pattern(regexp = "PENDIENTE|CONFIRMADA|CANCELADA")
    private String estado = "PENDIENTE";

    @Column
    @Min(0) @Max(5)
    private Float valoracion;

    @Column(name = "duracion_estimada")
    private Integer duracionEstimada;

    @Column(name = "recordatorio_activo")
    private Boolean recordatorioActivo;

    // Relación N:1 con Cliente
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    // Relación N:1 con Inmueble
    @ManyToOne
    @JoinColumn(name = "inmueble_id")
    private Inmueble inmueble;
}
