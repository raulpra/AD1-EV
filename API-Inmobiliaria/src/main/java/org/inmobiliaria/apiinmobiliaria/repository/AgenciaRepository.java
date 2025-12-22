package org.inmobiliaria.apiinmobiliaria.repository;

import org.inmobiliaria.apiinmobiliaria.domain.Agencia;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgenciaRepository extends CrudRepository<Agencia, Long> {

    List<Agencia> findByNombreContainingIgnoreCaseAndCodigoPostalAndAbiertoSabados(String nombre, Integer codigoPostal, Boolean abiertoSabados);
    List<Agencia> findAll();

    @Query(value = "SELECT a FROM Agencia a WHERE a.facturacionAnual > :facturacion AND a.abiertoSabados = true")
    List<Agencia> findAgenciasMaximaFacturacionAndAbiertoSabados(Double facturacion);
}
