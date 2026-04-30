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
@Table(name = "visita")
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
    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "PENDIENTE|CONFIRMADA|CANCELADA")
    private String estado = "PENDIENTE";

    @Column
    @Min(0) @Max(5)
    private Float valoracion;

    @Column(name = "duracion_estimada")
    private Integer duracionEstimada;

    @Column(name = "recordatorio_activo")
    @NotNull(message = "Debe indicar si el recordatorio está activo")
    private Boolean recordatorioActivo;

    // Relación N:1 con Cliente
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    // Relación N:1 con Inmueble
    @ManyToOne
    @JoinColumn(name = "inmueble_id")
    private Inmueble inmueble;

    public Long getClienteId() {
        return (this.cliente != null) ? this.cliente.getId() : null;
    }

    public Long getInmuebleId() {
        return (this.inmueble != null) ? this.inmueble.getId() : null;
    }
}
