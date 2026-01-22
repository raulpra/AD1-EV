package org.inmobiliaria.apiinmobiliaria.repository;

import org.inmobiliaria.apiinmobiliaria.domain.Inmueble;
import org.inmobiliaria.apiinmobiliaria.domain.Visita;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitaRepository extends CrudRepository<Visita,Long> {

    //List<Visita> findByEstadoAndFechaHoraGreaterThanEqualAndValoracionGreaterThanEqual(String estado, LocalDateTime fechaDesde, Float valoracionMin);
    @Query("SELECT v FROM Visita v WHERE " +
            "(:estado IS NULL OR v.estado = :estado) AND " +
            "(:fechaDesde IS NULL OR v.fechaHora >= :fechaDesde) AND " +
            "(:valoracionMin IS NULL OR v.valoracion >= :valoracionMin)")
    List<Visita> findByFilter(String estado, LocalDateTime fechaDesde, Float valoracionMin);

    List<Visita> findAll();

    //SQL Nativo
    @NativeQuery(value = "SELECT * FROM visita WHERE fecha_hora < CURRENT_TIMESTAMP")
    List<Visita> findVisitasPasadasNativas();

}
