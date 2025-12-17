package org.inmobiliaria.apiinmobiliaria;

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
import org.inmobiliaria.apiinmobiliaria.service.VisitaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VisitaServiceTests {

    @InjectMocks
    private VisitaService visitaService;

    @Mock
    private VisitaRepository visitaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private InmuebleRepository inmuebleRepository;

    @Mock
    private ModelMapper modelMapper;

    // ----------------------------------------------------------------
    // TEST FIND ALL (Sin filtros)
    // ----------------------------------------------------------------
    @Test
    public void testFindAll() {
        List<Visita> mockLista = List.of(new Visita());
        List<VisitaOutDto> mockOutDtos = List.of(new VisitaOutDto());

        when(visitaRepository.findAll()).thenReturn(mockLista);
        when(modelMapper.map(mockLista, new TypeToken<List<VisitaOutDto>>() {}.getType())).thenReturn(mockOutDtos);

        List<VisitaOutDto> result = visitaService.findAll(null, null, null);

        assertEquals(1, result.size());
        verify(visitaRepository).findAll();
    }

    // ----------------------------------------------------------------
    // TEST FIND ALL (Con filtros: Estado, Fecha, Valoracion)
    // ----------------------------------------------------------------
    @Test
    public void testFindAllWithFilters() {
        List<Visita> mockLista = List.of(new Visita());
        List<VisitaOutDto> mockOutDtos = List.of(new VisitaOutDto());

        String estado = "PENDIENTE";
        LocalDateTime fecha = LocalDateTime.now();
        Float valoracion = 4.0f;

        // Verifica que tu Repo tenga este método exacto
        when(visitaRepository.findByEstadoAndFechaHoraGreaterThanEqualAndValoracionGreaterThanEqual(estado, fecha, valoracion))
                .thenReturn(mockLista);

        when(modelMapper.map(mockLista, new TypeToken<List<VisitaOutDto>>() {}.getType())).thenReturn(mockOutDtos);

        List<VisitaOutDto> result = visitaService.findAll(estado, fecha, valoracion);

        assertEquals(1, result.size());
        verify(visitaRepository).findByEstadoAndFechaHoraGreaterThanEqualAndValoracionGreaterThanEqual(estado, fecha, valoracion);
    }

    // ----------------------------------------------------------------
    // TEST FIND BY ID
    // ----------------------------------------------------------------
    @Test
    public void testFindById() throws VisitaNotFoundException {
        Long id = 1L;
        Visita visita = new Visita(); visita.setId(id); visita.setComentarios("Test");
        VisitaOutDto dto = new VisitaOutDto(); dto.setId(id); dto.setComentarios("Test");

        when(visitaRepository.findById(id)).thenReturn(Optional.of(visita));
        when(modelMapper.map(visita, VisitaOutDto.class)).thenReturn(dto);

        VisitaOutDto result = visitaService.findById(id);

        assertEquals("Test", result.getComentarios());
    }

    @Test
    public void testFindByIdNotFound() {
        when(visitaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(VisitaNotFoundException.class, () -> visitaService.findById(99L));
    }

    // ----------------------------------------------------------------
    // TEST ADD (DTO -> DTO + Relaciones)
    // ----------------------------------------------------------------
    @Test
    public void testAdd() {
        // Datos de entrada
        VisitaInDto inDto = new VisitaInDto(LocalDateTime.now(), "Visita", "PENDIENTE", 5f, 30, true, 10L, 20L);

        // Mocks de relaciones
        Cliente mockCliente = new Cliente(); mockCliente.setId(10L);
        Inmueble mockInmueble = new Inmueble(); mockInmueble.setId(20L);

        // Entidad guardada
        Visita guardada = new Visita(1L, LocalDateTime.now(), "Visita", "PENDIENTE", 5f, 30, true, mockCliente, mockInmueble);
        VisitaOutDto outDto = new VisitaOutDto(1L, LocalDateTime.now(), "Visita", "PENDIENTE", 5f, 30, true, 10L, 20L);

        // Configuración mocks
        when(clienteRepository.findById(10L)).thenReturn(Optional.of(mockCliente));
        when(inmuebleRepository.findById(20L)).thenReturn(Optional.of(mockInmueble));

        // Strict stubbing: le decimos que ignore el mapeo void
        doNothing().when(modelMapper).map(eq(inDto), any(Visita.class));

        when(visitaRepository.save(any(Visita.class))).thenReturn(guardada);
        when(modelMapper.map(guardada, VisitaOutDto.class)).thenReturn(outDto);

        // Ejecución
        VisitaOutDto result = visitaService.add(inDto);

        // Verificación
        assertEquals(1L, result.getId());
        verify(clienteRepository).findById(10L);
        verify(inmuebleRepository).findById(20L);
        verify(visitaRepository).save(any(Visita.class));
    }

    @Test
    public void testAddClienteNotFound() {
        VisitaInDto inDto = new VisitaInDto(); inDto.setClienteId(99L);
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ClienteNotFoundException.class, () -> visitaService.add(inDto));
    }

    // ----------------------------------------------------------------
    // TEST MODIFY
    // ----------------------------------------------------------------
    @Test
    public void testModify() throws VisitaNotFoundException {
        Long id = 1L;
        VisitaInDto inDto = new VisitaInDto(); inDto.setComentarios("Nuevo Comentario");

        Visita existente = new Visita(); existente.setId(id); existente.setComentarios("Viejo");
        Visita guardada = new Visita(); guardada.setId(id); guardada.setComentarios("Nuevo Comentario");
        VisitaOutDto outDto = new VisitaOutDto(); outDto.setId(id); outDto.setComentarios("Nuevo Comentario");

        when(visitaRepository.findById(id)).thenReturn(Optional.of(existente));

        doNothing().when(modelMapper).map(inDto, existente);

        when(visitaRepository.save(existente)).thenReturn(guardada);
        when(modelMapper.map(guardada, VisitaOutDto.class)).thenReturn(outDto);

        VisitaOutDto result = visitaService.modify(id, inDto);

        assertEquals("Nuevo Comentario", result.getComentarios());
        verify(visitaRepository).findById(id);
        verify(visitaRepository).save(existente);
    }

    @Test
    public void testModifyNotFound() {
        Long id = 99L;
        when(visitaRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(VisitaNotFoundException.class, () -> visitaService.modify(id, new VisitaInDto()));
        verify(visitaRepository, never()).save(any());
    }

    // ----------------------------------------------------------------
    // TEST DELETE
    // ----------------------------------------------------------------
    @Test
    public void testDelete() throws VisitaNotFoundException {
        Long id = 1L;
        Visita aBorrar = new Visita(); aBorrar.setId(id);

        when(visitaRepository.findById(id)).thenReturn(Optional.of(aBorrar));

        visitaService.delete(id);

        verify(visitaRepository).findById(id);
        verify(visitaRepository).delete(aBorrar);
    }

    @Test
    public void testDeleteNotFound() {
        Long id = 99L;
        when(visitaRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(VisitaNotFoundException.class, () -> visitaService.delete(id));
    }
}