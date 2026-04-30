package org.inmobiliaria.apiinmobiliaria.repository;

import org.inmobiliaria.apiinmobiliaria.domain.Agencia;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgenciaRepository extends CrudRepository<Agencia, Long> {

    /* Si dejamos este método para JPA haga la mágia no es válido si falta algún campo
    List<Agencia> findByNombreContainingIgnoreCaseAndCodigoPostalAndAbiertoSabados(String nombre, Integer codigoPostal, Boolean abiertoSabados);
    */

    @Query("SELECT a FROM Agencia a WHERE " +
            "(:nombre IS NULL OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
            "(:codigoPostal IS NULL OR a.codigoPostal = :codigoPostal) AND " +
            "(:abiertoSabados IS NULL OR a.abiertoSabados = :abiertoSabados)")
    List<Agencia> findByFilter(String nombre, Integer codigoPostal, Boolean abiertoSabados);

    List<Agencia> findAll();


    @Query(value = "SELECT a FROM Agencia a WHERE a.facturacionAnual > :facturacion AND a.abiertoSabados = true")
    List<Agencia> findAgenciasMaximaFacturacionAndAbiertoSabados(Double facturacion);
}
