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
    @NotBlank(message = "El DNI es obligatorio")
    @Size(max = 20)
    private String dni;

    @Column
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @Column
    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 15)
    private String telefono;

    @Column
    @NotNull(message = "La comisión es obligatoria")
    @Min(value = 0, message= "El valor tiene que ser positivo" )
    private Float comision;

    @Column(name = "es_empresa")
    @NotNull(message = "Debe indicarse si es empresa")
    private Boolean esEmpresa;

    @Column(name = "fecha_alta")
    @NotNull(message = "La fecha de alta es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaAlta;

    // Relación: Un propietario tiene muchos inmuebles
    @OneToMany(mappedBy = "propietario")
    @JsonBackReference
    private List<Inmueble> inmuebles;
}
