package org.inmobiliaria.apiinmobiliaria.service;

import org.inmobiliaria.apiinmobiliaria.domain.Visita;
import org.inmobiliaria.apiinmobiliaria.dto.VisitaInDto;
import org.inmobiliaria.apiinmobiliaria.dto.VisitaOutDto;
import org.inmobiliaria.apiinmobiliaria.exception.VisitaNotFoundException;
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
    private ModelMapper modelMapper;

    // ADD
    public Visita add(Visita visita) {
        return visitaRepository.save(visita);
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
            visitas = visitaRepository.findByEstadoAndFechaHoraGreaterThanEqualAndValoracionGreaterThanEqual(
                    estado, fechaDesde, valoracionMin
            );
        } else {
            visitas = visitas = visitaRepository.findAll();
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
}