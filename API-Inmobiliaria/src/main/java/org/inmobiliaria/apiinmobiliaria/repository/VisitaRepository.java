package org.inmobiliaria.apiinmobiliaria.repository;

import org.inmobiliaria.apiinmobiliaria.domain.Inmueble;
import org.inmobiliaria.apiinmobiliaria.domain.Visita;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitaRepository extends CrudRepository<Visita,Long> {

    List<Visita> findByFilter(String estado, LocalDateTime fechaDesde, Float valoracionMin);
    List<Visita> findAll();
}
