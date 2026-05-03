package org.inmobiliaria.apiinmobiliaria.repository;

import org.inmobiliaria.apiinmobiliaria.domain.ClienteV2;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepositoryV2 extends CrudRepository<ClienteV2, Long> {
}