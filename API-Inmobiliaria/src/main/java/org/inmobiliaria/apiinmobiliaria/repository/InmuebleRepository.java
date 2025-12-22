package org.inmobiliaria.apiinmobiliaria.repository;

import org.inmobiliaria.apiinmobiliaria.domain.Inmueble;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InmuebleRepository extends CrudRepository<Inmueble, Long> {

    List<Inmueble> findByPrecioLessThanEqualAndMetrosGreaterThanEqualAndAscensor(Float precioMax, Integer metrosMin, Boolean ascensor);
    List<Inmueble> findAll();

    @Query("SELECT i FROM Inmueble i WHERE i.precio BETWEEN :min AND :max")
    List<Inmueble> findInmueblesRangoPrecio(Float min, Float max);
}
