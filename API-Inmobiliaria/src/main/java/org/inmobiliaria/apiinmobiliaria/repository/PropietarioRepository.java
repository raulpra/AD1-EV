package org.inmobiliaria.apiinmobiliaria.repository;

import org.inmobiliaria.apiinmobiliaria.domain.Propietario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropietarioRepository  extends CrudRepository<Propietario,Long> {

    List<Propietario> findByDniContainingAndNombreContainingAndEsEmpresa(String dni, String nombre, Boolean esEmpresa);
    List<Propietario> findAll();
}
