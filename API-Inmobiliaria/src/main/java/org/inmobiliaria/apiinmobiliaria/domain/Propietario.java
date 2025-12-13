package org.inmobiliaria.apiinmobiliaria.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="Propietario") //Le indicamos cual va a ser la entidad.
@Table

public class Propietario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @NotNull
    @Size(max = 20)
    private String dni;

    @NotBlank
    @Size(max = 100)
    private String nombre;

    @NotBlank
    @Size(max = 15) // Coincide con tu SQL VARCHAR(15)
    private String telefono;

    @Column
    @Min(value = 0, message= "el valor tiene que ser positivo" )
    private Float comision;

    @Column(name = "es_empresa")
    private Boolean esEmpresa;

    @Column(name = "fecha_alta")
    private LocalDate fechaAlta;

    // Relación: Un propietario tiene muchos inmuebles
    @OneToMany(mappedBy = "propietario")
    @JsonBackReference
    private List<Inmueble> inmuebles;
}
