package org.inmobiliaria.apiinmobiliaria.service;

import org.inmobiliaria.apiinmobiliaria.domain.Propietario;
import org.inmobiliaria.apiinmobiliaria.dto.PropietarioInDto;
import org.inmobiliaria.apiinmobiliaria.dto.PropietarioOutDto;
import org.inmobiliaria.apiinmobiliaria.exception.PropietarioNotFoundException;
import org.inmobiliaria.apiinmobiliaria.repository.PropietarioRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropietarioService {

    @Autowired
    private PropietarioRepository propietarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    // ADD: Recibe Entidad, devuelve Entidad
    public Propietario add(Propietario propietario) {
        return propietarioRepository.save(propietario);
    }

    // DELETE
    public void delete(long id) throws PropietarioNotFoundException {
        Propietario propietario = propietarioRepository.findById(id)
                .orElseThrow(() -> new PropietarioNotFoundException("Propietario no encontrado con ID: " + id));

        propietarioRepository.delete(propietario);
    }

    // FIND ALL: Lógica condicional para filtros
    public List<PropietarioOutDto> findAll(String dni, String nombre, Boolean esEmpresa) {
        List<Propietario> propietarios;

        boolean hayFiltros = dni != null || nombre != null || esEmpresa != null;

        if (hayFiltros) {
            // Llama al método del repositorio que definimos con JPA estándar
            // Al ser AND, si uno es null aquí, la búsqueda será estricta
            propietarios = propietarioRepository.findByDniContainingAndNombreContainingAndEsEmpresa(
                    dni, nombre, esEmpresa
            );
        } else {
            propietarios = propietarioRepository.findAll();
        }

        return modelMapper.map(propietarios, new TypeToken<List<PropietarioOutDto>>() {}.getType());
    }

    // FIND BY ID
    public PropietarioOutDto findById(long id) throws PropietarioNotFoundException {
        Propietario propietario = propietarioRepository.findById(id)
                .orElseThrow(() -> new PropietarioNotFoundException("Propietario no encontrado con ID: " + id));

        return modelMapper.map(propietario, PropietarioOutDto.class);
    }

    // MODIFY
    public PropietarioOutDto modify(long id, PropietarioInDto propietarioInDto) throws PropietarioNotFoundException {
        Propietario propietarioExistente = propietarioRepository.findById(id)
                .orElseThrow(() -> new PropietarioNotFoundException("Propietario no encontrado con ID: " + id));

        // Mapeamos los cambios del DTO a la entidad existente
        modelMapper.map(propietarioInDto, propietarioExistente);

        // Aseguramos que el ID no cambie
        propietarioExistente.setId(id);

        Propietario propietarioGuardado = propietarioRepository.save(propietarioExistente);
        return modelMapper.map(propietarioGuardado, PropietarioOutDto.class);
    }
}