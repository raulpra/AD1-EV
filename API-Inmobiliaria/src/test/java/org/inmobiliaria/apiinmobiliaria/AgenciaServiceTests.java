package org.inmobiliaria.apiinmobiliaria;

import org.inmobiliaria.apiinmobiliaria.domain.Agencia;
import org.inmobiliaria.apiinmobiliaria.dto.AgenciaInDto;
import org.inmobiliaria.apiinmobiliaria.dto.AgenciaOutDto;
import org.inmobiliaria.apiinmobiliaria.exception.AgenciaNotFoundException;
import org.inmobiliaria.apiinmobiliaria.repository.AgenciaRepository;
import org.inmobiliaria.apiinmobiliaria.service.AgenciaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AgenciaServiceTests {

    @InjectMocks
    private AgenciaService agenciaService;

    @Mock
    private AgenciaRepository agenciaRepository;

    @Mock
    private ModelMapper modelMapper;

    // TEST ADD
    @Test
    public void testFindAll() {
        // Datos simulados (Entities)
        // Constructor: id, nombre, direccion, facturacion(Float), cp, sabados, fecha, listaInmuebles(null)
        List<Agencia> mockAgenciaList = List.of(
                new Agencia(1L, "Inmobiliaria Norte", "Calle A, 1", 1000.0f, 28001, true, LocalDate.of(2010, 1, 1), null),
                new Agencia(2L, "Inmobiliaria Sur", "Calle B, 2", 2000.0f, 28002, false, LocalDate.of(2015, 5, 5), null)
        );

        // Respuesta esperada (DTOs)
        // Constructor: id, nombre, direccion, facturacion, cp, sabados, fecha (SIN lista)
        List<AgenciaOutDto> modelMapperOut = List.of(
                new AgenciaOutDto(1L, "Inmobiliaria Norte", "Calle A, 1", 1000.0f, 28001, true, LocalDate.of(2010, 1, 1)),
                new AgenciaOutDto(2L, "Inmobiliaria Sur", "Calle B, 2", 2000.0f, 28002, false, LocalDate.of(2015, 5, 5))
        );

        // Mocking
        when(agenciaRepository.findAll()).thenReturn(mockAgenciaList);
        when(modelMapper.map(mockAgenciaList, new TypeToken<List<AgenciaOutDto>>() {}.getType())).thenReturn(modelMapperOut);

        // Ejecución
        List<AgenciaOutDto> actualAgenciaList = agenciaService.findAll(null, null, null);

        // Aserciones
        assertEquals(2, actualAgenciaList.size());
        assertEquals("Inmobiliaria Norte", actualAgenciaList.getFirst().getNombre());

        verify(agenciaRepository, times(1)).findAll();
    }

    @Test
    public void testFindAllWithFilters() {
        // Entity con 'null' al final
        List<Agencia> mockAgenciaList = List.of(
                new Agencia(1L, "Inmobiliaria Norte", "Calle A, 1", 1000.0f, 28001, true, LocalDate.of(2010, 1, 1), null)
        );

        // DTO sin lista
        List<AgenciaOutDto> mockModelMapperOut = List.of(
                new AgenciaOutDto(1L, "Inmobiliaria Norte", "Calle A, 1", 1000.0f, 28001, true, LocalDate.of(2010, 1, 1))
        );

        // Mocking para filtros
        String nombre = "Norte";
        Integer cp = 28001;
        Boolean sabados = true;

        when(agenciaRepository.findByNombreContainingIgnoreCaseAndCodigoPostalAndAbiertoSabados(nombre, cp, sabados))
                .thenReturn(mockAgenciaList);
        when(modelMapper.map(mockAgenciaList, new TypeToken<List<AgenciaOutDto>>() {}.getType())).thenReturn(mockModelMapperOut);

        // Ejecución
        List<AgenciaOutDto> actualAgenciaList = agenciaService.findAll(nombre, cp, sabados);

        // Aserciones
        assertEquals(1, actualAgenciaList.size());

        // Verificamos que llamó al método con filtros y NO al findAll()
        verify(agenciaRepository, times(0)).findAll();
        verify(agenciaRepository, times(1)).findByNombreContainingIgnoreCaseAndCodigoPostalAndAbiertoSabados(nombre, cp, sabados);
    }

    @Test
    public void testFindById() throws AgenciaNotFoundException {
        // Entity con null al final
        Agencia mockAgencia = new Agencia(1L, "Inmobiliaria Norte", "Calle A, 1", 1000.0f, 28001, true, LocalDate.of(2010, 1, 1), null);

        // DTO normal
        AgenciaOutDto mockOutDto = new AgenciaOutDto(1L, "Inmobiliaria Norte", "Calle A, 1", 1000.0f, 28001, true, LocalDate.of(2010, 1, 1));

        when(agenciaRepository.findById(1L)).thenReturn(Optional.of(mockAgencia));
        when(modelMapper.map(mockAgencia, AgenciaOutDto.class)).thenReturn(mockOutDto);

        AgenciaOutDto result = agenciaService.findById(1L);

        assertEquals("Inmobiliaria Norte", result.getNombre());
        verify(agenciaRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindByIdNotFound() {
        when(agenciaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(AgenciaNotFoundException.class, () -> {
            agenciaService.findById(99L);
        });

        verify(agenciaRepository, times(1)).findById(99L);
    }

    // El método Add recibe Entidad y devuelve Entidad)
    @Test
    public void testAdd() {
        // Datos de entrada (Entidad sin ID)
        Agencia agenciaInput = new Agencia(0, "Nueva Agencia", "Calle X", 1000f, 28000, true, LocalDate.now(), null);

        // Dato esperado (Entidad con ID generado)
        Agencia agenciaGuardada = new Agencia(1L, "Nueva Agencia", "Calle X", 1000f, 28000, true, LocalDate.now(), null);

        // Mocking
        // Aquí NO mockeamos modelMapper porque add no lo usa.
        when(agenciaRepository.save(agenciaInput)).thenReturn(agenciaGuardada);

        // Ejecución
        Agencia resultado = agenciaService.add(agenciaInput);

        // Aserciones
        assertEquals(1L, resultado.getId());
        assertEquals("Nueva Agencia", resultado.getNombre());

        verify(agenciaRepository, times(1)).save(agenciaInput);
    }

    // TEST MODIFY (este método recibe DTO y devuelve DTO)
    @Test
    public void testModify() {
        Long id = 1L;

        // DTO de entrada (Datos nuevos)
        AgenciaInDto inDto = new AgenciaInDto("Nombre Modificado", "Dir Modificada", 5000f, 28005, false, LocalDate.now());

        // Entidad existente en BD (Datos viejos)
        Agencia agenciaExistente = new Agencia(id, "Nombre Viejo", "Dir Vieja", 1000f, 28000, true, LocalDate.now(), null);

        // Entidad tras guardar (Datos nuevos + ID asegurado)
        Agencia agenciaGuardada = new Agencia(id, "Nombre Modificado", "Dir Modificada", 5000f, 28005, false, LocalDate.now(), null);

        // DTO de salida esperado
        AgenciaOutDto outDto = new AgenciaOutDto(id, "Nombre Modificado", "Dir Modificada", 5000f, 28005, false, LocalDate.now());

        // Mocking
        when(agenciaRepository.findById(id)).thenReturn(Optional.of(agenciaExistente));

        // Le decimos a Mockito que cuando el servicio intente volcar los datos  no haga nada y no se queje.
        doNothing().when(modelMapper).map(inDto, agenciaExistente);

        // Guardar
        when(agenciaRepository.save(agenciaExistente)).thenReturn(agenciaGuardada);

        // Convertir resultado a OutDto
        when(modelMapper.map(agenciaGuardada, AgenciaOutDto.class)).thenReturn(outDto);

        // Ejecución
        AgenciaOutDto resultado = agenciaService.modify(id, inDto);

        // Aserciones
        assertEquals("Nombre Modificado", resultado.getNombre());

        // Verificamos el flujo completo
        verify(agenciaRepository).findById(id);
        verify(modelMapper).map(inDto, agenciaExistente);
        verify(agenciaRepository).save(agenciaExistente);
    }

    @Test
    public void testModifyNotFound() {
        Long id = 99L;
        AgenciaInDto inDto = new AgenciaInDto();

        when(agenciaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AgenciaNotFoundException.class, () -> {
            agenciaService.modify(id, inDto);
        });

        verify(agenciaRepository, never()).save(any());
    }

    // TEST DELETE (El método busca primero y luego borra la entidad)
    @Test
    public void testDelete() throws AgenciaNotFoundException {
        Long id = 1L;
        Agencia agenciaParaBorrar = new Agencia(id, "A Borrar", "C/ Borrar", 0f, 0, false, LocalDate.now(), null);

        // Mocking
        when(agenciaRepository.findById(id)).thenReturn(Optional.of(agenciaParaBorrar));

        // Ejecución
        agenciaService.delete(id);

        // Verificación
        verify(agenciaRepository, times(1)).findById(id);
        // OJO: Tu servicio usa delete(entidad), NO deleteById(id)
        verify(agenciaRepository, times(1)).delete(agenciaParaBorrar);
    }

    @Test
    public void testDeleteNotFound() {
        Long id = 99L;

        when(agenciaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AgenciaNotFoundException.class, () -> {
            agenciaService.delete(id);
        });

        // Aseguramos que no se llamó a borrar nada
        verify(agenciaRepository, never()).delete(any());
    }

    @Test
    public void testFindAgenciasMaximaFacturacionAndAbiertoSabados() {
        List<Agencia> mockAgenciaList = List.of(
                new Agencia(1L, "Inmobiliaria Top", "Calle Lujo, 1", 500000.0f, 28001, true, LocalDate.of(2015, 1, 1), null)
        );

        List<AgenciaOutDto> mockModelMapperOut = List.of(
                new AgenciaOutDto(1L, "Inmobiliaria Top", "Calle Lujo, 1", 500000.0f, 28001, true, LocalDate.of(2015, 1, 1))
        );

        Double minFacturacion = 300000.0;

        when(agenciaRepository.findAgenciasMaximaFacturacionAndAbiertoSabados(minFacturacion))
                .thenReturn(mockAgenciaList);

        when(modelMapper.map(mockAgenciaList, new TypeToken<List<AgenciaOutDto>>() {}.getType()))
                .thenReturn(mockModelMapperOut);

        List<AgenciaOutDto> actualAgenciaList = agenciaService.findAgenciasMaximaFacturacionAndAbiertoSabados(minFacturacion);

        assertEquals(1, actualAgenciaList.size());
        assertEquals("Inmobiliaria Top", actualAgenciaList.get(0).getNombre());

        verify(agenciaRepository, times(0)).findAll();
        verify(agenciaRepository, times(1)).findAgenciasMaximaFacturacionAndAbiertoSabados(minFacturacion);

    }
}