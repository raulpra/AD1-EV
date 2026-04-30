package org.inmobiliaria.apiinmobiliaria.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data  //Para llamar a lombok que nos va a crear todos los getter y setter
@AllArgsConstructor //Creamos todos los constructores
@NoArgsConstructor //Creamos todos los constructores tb sin argumentos.
@Entity(name="Agencia") //Le indicamos cual va a ser la entidad.
@Table

public class Agencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @Column
    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 200)
    private String direccion;

    @Column(name = "facturacion_anual")
    @NotNull(message = "La facturación es obligatoria")
    @Min(value=0, message="la facturación debe ser positiva")
    private Float facturacionAnual;

    @Column(name = "codigo_postal")
    @NotNull(message = "El código postal es obligatorio")
    private Integer codigoPostal;

    @Column(name = "abierto_sabados")
    @NotNull(message = "Debe indicarse si abre sábados")
    private Boolean abiertoSabados;

    @Column(name = "fecha_fundacion")
    @NotNull(message = "La fecha de fundación es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFundacion;

    // Relación: Una agencia tiene muchos inmuebles
    @OneToMany(mappedBy = "agencia", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Inmueble> inmuebles;
}
