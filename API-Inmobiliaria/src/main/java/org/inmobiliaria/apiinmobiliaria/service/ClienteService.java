package org.inmobiliaria.apiinmobiliaria.service;

import org.inmobiliaria.apiinmobiliaria.domain.Cliente;
import org.inmobiliaria.apiinmobiliaria.dto.AgenciaOutDto;
import org.inmobiliaria.apiinmobiliaria.dto.ClienteInDto;
import org.inmobiliaria.apiinmobiliaria.dto.ClienteOutDto;
import org.inmobiliaria.apiinmobiliaria.exception.ClienteNotFoundException;
import org.inmobiliaria.apiinmobiliaria.repository.ClienteRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ModelMapper modelMapper;

    // ADD
    public Cliente add(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    // DELETE
    public void delete(long id) throws ClienteNotFoundException {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado con ID: " + id));

        clienteRepository.delete(cliente);
    }

    // FIND ALL (Filtros: Email, Teléfono, Suscrito)
    public List<ClienteOutDto> findAll(String email, String telefono, Boolean suscrito) {
        List<Cliente> clientes;

        boolean hayFiltros = email != null || telefono != null || suscrito != null;

        if (hayFiltros) {
            // Repositorio con JPA standard (email containing, telefono exact, suscrito exact)
            clientes = clienteRepository.findByEmailContainingAndTelefonoContainingAndSuscrito(
                    email, telefono, suscrito
            );
        } else {
            clientes = clienteRepository.findAll();
        }

        return modelMapper.map(clientes, new TypeToken<List<ClienteOutDto>>() {}.getType());
    }

    // FIND BY ID
    public ClienteOutDto findById(long id) throws ClienteNotFoundException {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado con ID: " + id));

        return modelMapper.map(cliente, ClienteOutDto.class);
    }

    // MODIFY
    public ClienteOutDto modify(long id, ClienteInDto clienteInDto) throws ClienteNotFoundException {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado con ID: " + id));

        modelMapper.map(clienteInDto, clienteExistente);
        clienteExistente.setId(id);

        Cliente clienteGuardado = clienteRepository.save(clienteExistente);
        return modelMapper.map(clienteGuardado, ClienteOutDto.class);
    }

    public List<ClienteOutDto> findClientesVip(Float presupuestoMinimo) {
        List<Cliente> clientes;
        clientes = clienteRepository.findClientesVip(presupuestoMinimo);
        return modelMapper.map(clientes, new TypeToken<List<ClienteOutDto>>() {}.getType());
    }
}
