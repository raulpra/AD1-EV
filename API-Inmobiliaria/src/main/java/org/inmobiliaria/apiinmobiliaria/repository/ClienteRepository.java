package org.inmobiliaria.apiinmobiliaria.repository;

import org.inmobiliaria.apiinmobiliaria.domain.Cliente;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends CrudRepository<Cliente, Long> {

    List<Cliente> findByEmailContainingAndTelefonoContainingAndSuscrito(String email, String telefono, Boolean suscrito);
    List<Cliente> findAll();

    @Query("SELECT c FROM Cliente c WHERE c.presupuestoMaximo > :cantidad AND c.suscrito = true")
    List<Cliente> findClientesVip(Float cantidad);

    String telefono(String telefono);
}
