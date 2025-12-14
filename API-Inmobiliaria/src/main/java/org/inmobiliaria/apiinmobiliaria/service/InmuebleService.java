package org.inmobiliaria.apiinmobiliaria.service;

import org.inmobiliaria.apiinmobiliaria.domain.Inmueble;
import org.inmobiliaria.apiinmobiliaria.dto.InmuebleInDto;
import org.inmobiliaria.apiinmobiliaria.dto.InmuebleOutDto;
import org.inmobiliaria.apiinmobiliaria.exception.InmuebleNotFoundException;
import org.inmobiliaria.apiinmobiliaria.repository.InmuebleRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InmuebleService {

    @Autowired
    private InmuebleRepository inmuebleRepository;

    @Autowired
    private ModelMapper modelMapper;

    // ADD (Recibe Entidad)
    public Inmueble add(Inmueble inmueble) {
        return inmuebleRepository.save(inmueble);
    }

    // DELETE
    public void delete(long id) throws InmuebleNotFoundException {
        Inmueble inmueble = inmuebleRepository.findById(id)
                .orElseThrow(() -> new InmuebleNotFoundException("Inmueble no encontrado con ID: " + id));

        inmuebleRepository.delete(inmueble);
    }

    // FIND ALL (Filtros: Precio Máximo, Metros Mínimos, Ascensor)
    public List<InmuebleOutDto> findAll(Float precioMax, Integer metrosMin, Boolean ascensor) {
        List<Inmueble> inmuebles;

        boolean hayFiltros = precioMax != null || metrosMin != null || ascensor != null;

        if (hayFiltros) {
            // Llama a método específico del repositorio (Precio <=, Metros >=, Ascensor =)
            inmuebles = inmuebleRepository.findByPrecioLessThanEqualAndMetrosGreaterThanEqualAndAscensor(
                    precioMax, metrosMin, ascensor
            );
        } else {
            inmuebles = inmuebleRepository.findAll();
        }

        return modelMapper.map(inmuebles, new TypeToken<List<InmuebleOutDto>>() {}.getType());
    }

    // FIND BY ID
    public InmuebleOutDto findById(long id) throws InmuebleNotFoundException {
        Inmueble inmueble = inmuebleRepository.findById(id)
                .orElseThrow(() -> new InmuebleNotFoundException("Inmueble no encontrado con ID: " + id));

        return modelMapper.map(inmueble, InmuebleOutDto.class);
    }

    // MODIFY (PODRÍA RECIBIR SOLO ENTIDAD)
    public InmuebleOutDto modify(long id, InmuebleInDto inmuebleInDto) throws InmuebleNotFoundException {
        Inmueble inmuebleExistente = inmuebleRepository.findById(id)
                .orElseThrow(() -> new InmuebleNotFoundException("Inmueble no encontrado con ID: " + id));

        // Mapeamos cambios
        modelMapper.map(inmuebleInDto, inmuebleExistente);

        // Aseguramos ID
        inmuebleExistente.setId(id);

        Inmueble inmuebleGuardado = inmuebleRepository.save(inmuebleExistente);
        return modelMapper.map(inmuebleGuardado, InmuebleOutDto.class);
    }
}