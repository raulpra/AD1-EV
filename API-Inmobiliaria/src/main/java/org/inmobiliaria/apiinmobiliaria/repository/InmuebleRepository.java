package org.inmobiliaria.apiinmobiliaria.repository;

import org.inmobiliaria.apiinmobiliaria.domain.Inmueble;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InmuebleRepository extends CrudRepository<Inmueble, Long> {

    List<Inmueble> findByFilter(Float precioMax, Integer metrosMin, Boolean ascensor);
    List<Inmueble> findAll();
}
