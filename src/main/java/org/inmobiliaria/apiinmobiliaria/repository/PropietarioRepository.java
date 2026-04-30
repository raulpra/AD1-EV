package org.inmobiliaria.apiinmobiliaria.repository;

import org.inmobiliaria.apiinmobiliaria.domain.Propietario;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropietarioRepository  extends CrudRepository<Propietario,Long> {

    //List<Propietario> findByDniContainingAndNombreContainingAndEsEmpresa(String dni, String nombre, Boolean esEmpresa);

    @Query("SELECT p FROM Propietario p WHERE " +
            "(:dni IS NULL OR LOWER(p.dni) LIKE LOWER(CONCAT('%', :dni, '%'))) AND " +
            "(:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
            "(:esEmpresa IS NULL OR p.esEmpresa = :esEmpresa)")
    List<Propietario> findByFilter(String dni, String nombre, Boolean esEmpresa);

    List<Propietario> findAll();


    @NativeQuery(value = "SELECT * FROM propietario WHERE es_empresa = true")
    List<Propietario> findEmpresasNativas();
}
