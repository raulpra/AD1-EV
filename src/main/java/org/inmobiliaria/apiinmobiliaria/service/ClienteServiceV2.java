package org.inmobiliaria.apiinmobiliaria.service;

import org.inmobiliaria.apiinmobiliaria.domain.ClienteV2;
import org.inmobiliaria.apiinmobiliaria.dto.ClienteInDtoV2;
import org.inmobiliaria.apiinmobiliaria.dto.ClienteOutDtoV2;
import org.inmobiliaria.apiinmobiliaria.exception.ClienteNotFoundException;
import org.inmobiliaria.apiinmobiliaria.repository.ClienteRepositoryV2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class ClienteServiceV2 {

    @Autowired
    private ClienteRepositoryV2 clienteRepositoryV2;

    @Autowired
    private ModelMapper modelMapper;

    public ClienteOutDtoV2 findById(long id) throws ClienteNotFoundException {
        ClienteV2 cliente = clienteRepositoryV2.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente V2 no encontrado con ID: " + id));

        ClienteOutDtoV2 outDto = modelMapper.map(cliente, ClienteOutDtoV2.class);
        // Calculamos los días registrados
        if (cliente.getFechaAlta() != null) {
            long dias = ChronoUnit.DAYS.between(cliente.getFechaAlta(), LocalDate.now());
            outDto.setDiasRegistrado(Math.max(0, dias));
        }
        return outDto;
    }

    public ClienteOutDtoV2 add(ClienteInDtoV2 inDto) {
        ClienteV2 cliente = modelMapper.map(inDto, ClienteV2.class);
        ClienteV2 guardado = clienteRepositoryV2.save(cliente);
        return modelMapper.map(guardado, ClienteOutDtoV2.class);
    }

    public ClienteOutDtoV2 modify(long id, ClienteInDtoV2 inDto) throws ClienteNotFoundException {
        ClienteV2 clienteExistente = clienteRepositoryV2.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente V2 no encontrado con ID: " + id));

        // LÓGICA V2: Se actualiza todo EXCEPTO el email y la fecha de alta (se mantienen los originales)
        String emailOriginal = clienteExistente.getEmail();
        LocalDate fechaAltaOriginal = clienteExistente.getFechaAlta();

        modelMapper.map(inDto, clienteExistente);

        clienteExistente.setId(id);
        clienteExistente.setEmail(emailOriginal);
        clienteExistente.setFechaAlta(fechaAltaOriginal);

        ClienteV2 modificado = clienteRepositoryV2.save(clienteExistente);
        return modelMapper.map(modificado, ClienteOutDtoV2.class);
    }

    public void delete(long id) throws ClienteNotFoundException {
        ClienteV2 cliente = clienteRepositoryV2.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente V2 no encontrado con ID: " + id));
        clienteRepositoryV2.delete(cliente);
    }
}
