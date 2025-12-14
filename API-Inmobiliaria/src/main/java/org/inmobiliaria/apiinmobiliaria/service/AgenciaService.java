package org.inmobiliaria.apiinmobiliaria.service;

import org.inmobiliaria.apiinmobiliaria.domain.Agencia;
import org.inmobiliaria.apiinmobiliaria.dto.AgenciaInDto;
import org.inmobiliaria.apiinmobiliaria.dto.AgenciaOutDto;
import org.inmobiliaria.apiinmobiliaria.exception.AgenciaNotFoundException;
import org.inmobiliaria.apiinmobiliaria.repository.AgenciaRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgenciaService {

    @Autowired
    private AgenciaRepository agenciaRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Agencia add(Agencia agencia){
        return agenciaRepository.save(agencia);
    }

    public void delete(long id) throws AgenciaNotFoundException {
        Agencia agencia = agenciaRepository.findById(id)
                .orElseThrow(() -> new AgenciaNotFoundException("Agencia no encontrada con ID: " + id));

        agenciaRepository.delete(agencia);
    }

    public List<AgenciaOutDto> findAll(String nombre, Integer codigoPostal, Boolean abiertoSabados) {
        List<Agencia> agencias;

        // Si nos envían los datos de filtro, usamos el método específico.
        // Si todo es nulo o vacío, usamos findAll().
        boolean hayFiltros = nombre != null || codigoPostal != null || abiertoSabados != null;

        if (hayFiltros) {
            // aquí debes pasar LOS 3 parámetros.
            // Si uno es null, la búsqueda podría no devolver nada.
            agencias = agenciaRepository.findByNombreContainingAndCodigoPostalAndAbiertoSabados(
                    nombre, codigoPostal, abiertoSabados
            );
        } else {
            // Si no hay filtros, devolvemos todas
            agencias = agenciaRepository.findAll();
        }

        // Convertimos la lista de Agencias a AgenciaOutDto usando ModelMapper y TypeToken
        return modelMapper.map(agencias, new TypeToken<List<AgenciaOutDto>>() {}.getType());
    }


    public AgenciaOutDto findById(long id) throws AgenciaNotFoundException {
        Agencia agencia = agenciaRepository.findById(id)
                .orElseThrow(() -> new AgenciaNotFoundException("Agencia no encontrada con ID: " + id));

        return modelMapper.map(agencia, AgenciaOutDto.class);
    }


    public AgenciaOutDto modify(long id, AgenciaInDto agenciaInDto) {
        Agencia agenciaExistente = agenciaRepository.findById(id)
                .orElseThrow(() -> new AgenciaNotFoundException("Agencia no encontrada con ID: " + id));

        // ModelMapper vuelca los datos del DTO sobre la entidad existente
        modelMapper.map(agenciaInDto, agenciaExistente);

        // Aseguramos el ID
        agenciaExistente.setId(id);

        Agencia agenciaGuardada = agenciaRepository.save(agenciaExistente);
        return modelMapper.map(agenciaGuardada, AgenciaOutDto.class);
    }
}
