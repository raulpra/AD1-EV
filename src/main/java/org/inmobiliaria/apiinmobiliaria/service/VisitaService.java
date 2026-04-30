package org.inmobiliaria.apiinmobiliaria.service;

import org.inmobiliaria.apiinmobiliaria.domain.Cliente;
import org.inmobiliaria.apiinmobiliaria.domain.Inmueble;
import org.inmobiliaria.apiinmobiliaria.domain.Visita;
import org.inmobiliaria.apiinmobiliaria.dto.VisitaInDto;
import org.inmobiliaria.apiinmobiliaria.dto.VisitaOutDto;
import org.inmobiliaria.apiinmobiliaria.exception.ClienteNotFoundException;
import org.inmobiliaria.apiinmobiliaria.exception.InmuebleNotFoundException;
import org.inmobiliaria.apiinmobiliaria.exception.VisitaNotFoundException;
import org.inmobiliaria.apiinmobiliaria.repository.ClienteRepository;
import org.inmobiliaria.apiinmobiliaria.repository.InmuebleRepository;
import org.inmobiliaria.apiinmobiliaria.repository.VisitaRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VisitaService {

    @Autowired
    private VisitaRepository visitaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private InmuebleRepository inmuebleRepository;

    @Autowired
    private ModelMapper modelMapper;

    // ADD
    public VisitaOutDto add(VisitaInDto visitaInDto) {
        // 1. Buscamos el Cliente por su ID
        Cliente cliente = clienteRepository.findById(visitaInDto.getClienteId())
                .orElseThrow(() -> new ClienteNotFoundException("El cliente con ID " + visitaInDto.getClienteId() + " no existe"));

        // 2. Buscamos el Inmueble por su ID
        Inmueble inmueble = inmuebleRepository.findById(visitaInDto.getInmuebleId())
                .orElseThrow(() -> new InmuebleNotFoundException("El inmueble con ID " + visitaInDto.getInmuebleId() + " no existe"));

        // 3. Creamos la Visita y mapeamos los datos básicos (fecha, comentario, etc.)
        Visita visita = new Visita();
        modelMapper.map(visitaInDto, visita);

        // 4. ASIGNAMOS LAS RELACIONES
        visita.setCliente(cliente);
        visita.setInmueble(inmueble);

        // 5. Guardamos y devolvemos DTO
        Visita visitaGuardada = visitaRepository.save(visita);
        return modelMapper.map(visitaGuardada, VisitaOutDto.class);
    }

    // DELETE
    public void delete(long id) throws VisitaNotFoundException {
        Visita visita = visitaRepository.findById(id)
                .orElseThrow(() -> new VisitaNotFoundException("Visita no encontrada con ID: " + id));

        visitaRepository.delete(visita);
    }

    // FIND ALL (Filtros: Estado, Fecha Desde, Valoración Mínima)
    public List<VisitaOutDto> findAll(String estado, LocalDateTime fechaDesde, Float valoracionMin) {
        List<Visita> visitas;

        boolean hayFiltros = estado != null || fechaDesde != null || valoracionMin != null;

        if (hayFiltros) {
            /*
            visitas = visitaRepository.findByEstadoAndFechaHoraGreaterThanEqualAndValoracionGreaterThanEqual(
                    estado, fechaDesde, valoracionMin);*/
            visitas = visitaRepository.findByFilter(estado, fechaDesde, valoracionMin);
        } else {
            visitas = visitaRepository.findAll();
        }

        return modelMapper.map(visitas, new TypeToken<List<VisitaOutDto>>() {}.getType());
    }

    // FIND BY ID
    public VisitaOutDto findById(long id) throws VisitaNotFoundException {
        Visita visita = visitaRepository.findById(id)
                .orElseThrow(() -> new VisitaNotFoundException("Visita no encontrada con ID: " + id));

        return modelMapper.map(visita, VisitaOutDto.class);
    }

    // MODIFY
    public VisitaOutDto modify(long id, VisitaInDto visitaInDto) throws VisitaNotFoundException {
        Visita visitaExistente = visitaRepository.findById(id)
                .orElseThrow(() -> new VisitaNotFoundException("Visita no encontrada con ID: " + id));

        modelMapper.map(visitaInDto, visitaExistente);
        visitaExistente.setId(id);

        Visita visitaGuardada = visitaRepository.save(visitaExistente);
        return modelMapper.map(visitaGuardada, VisitaOutDto.class);
    }

    // SQL FIND VISITAS PASADAS
    public List<VisitaOutDto> getPasadasSql() {
        List<Visita> lista = visitaRepository.findVisitasPasadasNativas();
        return modelMapper.map(lista, new TypeToken<List<VisitaOutDto>>() {}.getType());
    }
}