package org.inmobiliaria.apiinmobiliaria.repository;

import org.inmobiliaria.apiinmobiliaria.domain.Agencia;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgenciaRepository extends CrudRepository<Agencia, Long> {

    List<Agencia> findByNombreContainingIgnoreCaseAndCodigoPostalAndAbiertoSabados(String nombre, Integer codigoPostal, Boolean abiertoSabados);
    List<Agencia> findAll();
}
