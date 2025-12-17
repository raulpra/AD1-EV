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

    @Test
    public void testFindAll() {
        // 1. Datos simulados (Entities)
        // Constructor: id, nombre, direccion, facturacion(Float), cp, sabados, fecha, listaInmuebles(null)
        List<Agencia> mockAgenciaList = List.of(
                new Agencia(1L, "Inmobiliaria Norte", "Calle A, 1", 1000.0f, 28001, true, LocalDate.of(2010, 1, 1), null),
                new Agencia(2L, "Inmobiliaria Sur", "Calle B, 2", 2000.0f, 28002, false, LocalDate.of(2015, 5, 5), null)
        );

        // 2. Respuesta esperada (DTOs)
        // Constructor: id, nombre, direccion, facturacion, cp, sabados, fecha (SIN lista)
        List<AgenciaOutDto> modelMapperOut = List.of(
                new AgenciaOutDto(1L, "Inmobiliaria Norte", "Calle A, 1", 1000.0f, 28001, true, LocalDate.of(2010, 1, 1)),
                new AgenciaOutDto(2L, "Inmobiliaria Sur", "Calle B, 2", 2000.0f, 28002, false, LocalDate.of(2015, 5, 5))
        );

        // 3. Mocking
        // Caso sin filtros -> llama a findAll()
        when(agenciaRepository.findAll()).thenReturn(mockAgenciaList);
        when(modelMapper.map(mockAgenciaList, new TypeToken<List<AgenciaOutDto>>() {}.getType())).thenReturn(modelMapperOut);

        // 4. Ejecución (pasamos null para activar la rama del "else" en tu servicio)
        List<AgenciaOutDto> actualAgenciaList = agenciaService.findAll(null, null, null);

        // 5. Aserciones
        assertEquals(2, actualAgenciaList.size());
        assertEquals("Inmobiliaria Norte", actualAgenciaList.getFirst().getNombre());

        verify(agenciaRepository, times(1)).findAll();
    }

    @Test
    public void testFindAllWithFilters() {
        // 1. Entity con 'null' al final
        List<Agencia> mockAgenciaList = List.of(
                new Agencia(1L, "Inmobiliaria Norte", "Calle A, 1", 1000.0f, 28001, true, LocalDate.of(2010, 1, 1), null)
        );

        // 2. DTO sin lista
        List<AgenciaOutDto> mockModelMapperOut = List.of(
                new AgenciaOutDto(1L, "Inmobiliaria Norte", "Calle A, 1", 1000.0f, 28001, true, LocalDate.of(2010, 1, 1))
        );

        // 3. Mocking para filtros
        String nombre = "Norte";
        Integer cp = 28001;
        Boolean sabados = true;

        when(agenciaRepository.findByNombreContainingIgnoreCaseAndCodigoPostalAndAbiertoSabados(nombre, cp, sabados))
                .thenReturn(mockAgenciaList);
        when(modelMapper.map(mockAgenciaList, new TypeToken<List<AgenciaOutDto>>() {}.getType())).thenReturn(mockModelMapperOut);

        // 4. Ejecución
        List<AgenciaOutDto> actualAgenciaList = agenciaService.findAll(nombre, cp, sabados);

        // 5. Aserciones
        assertEquals(1, actualAgenciaList.size());

        // Verificamos que llamó al método con filtros y NO al findAll() genérico
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

    // ----------------------------------------------------------------
    // TEST ADD (Tu método recibe Entidad y devuelve Entidad)
    // ----------------------------------------------------------------
    @Test
    public void testAdd() {
        // 1. Datos de entrada (Entidad sin ID)
        Agencia agenciaInput = new Agencia(0, "Nueva Agencia", "Calle X", 1000f, 28000, true, LocalDate.now(), null);

        // 2. Dato esperado (Entidad con ID generado)
        Agencia agenciaGuardada = new Agencia(1L, "Nueva Agencia", "Calle X", 1000f, 28000, true, LocalDate.now(), null);

        // 3. Mocking
        // OJO: Aquí NO mockeamos modelMapper porque tu método 'add' no lo usa.
        when(agenciaRepository.save(agenciaInput)).thenReturn(agenciaGuardada);

        // 4. Ejecución
        Agencia resultado = agenciaService.add(agenciaInput);

        // 5. Aserciones
        assertEquals(1L, resultado.getId());
        assertEquals("Nueva Agencia", resultado.getNombre());

        verify(agenciaRepository, times(1)).save(agenciaInput);
    }

    // ----------------------------------------------------------------
    // TEST MODIFY (Tu método recibe DTO y devuelve DTO)
    // ----------------------------------------------------------------
    @Test
    public void testModify() {
        Long id = 1L;

        // 1. DTO de entrada (Datos nuevos)
        AgenciaInDto inDto = new AgenciaInDto("Nombre Modificado", "Dir Modificada", 5000f, 28005, false, LocalDate.now());

        // 2. Entidad existente en BD (Datos viejos)
        Agencia agenciaExistente = new Agencia(id, "Nombre Viejo", "Dir Vieja", 1000f, 28000, true, LocalDate.now(), null);

        // 3. Entidad tras guardar (Datos nuevos + ID asegurado)
        Agencia agenciaGuardada = new Agencia(id, "Nombre Modificado", "Dir Modificada", 5000f, 28005, false, LocalDate.now(), null);

        // 4. DTO de salida esperado
        AgenciaOutDto outDto = new AgenciaOutDto(id, "Nombre Modificado", "Dir Modificada", 5000f, 28005, false, LocalDate.now());

        // 5. Mocking
        // Paso A: Encontrar la agencia
        when(agenciaRepository.findById(id)).thenReturn(Optional.of(agenciaExistente));

        // --- CORRECCIÓN AQUÍ ---
        // Paso B: Le decimos a Mockito que cuando el servicio intente volcar los datos (void), no haga nada y no se queje.
        doNothing().when(modelMapper).map(inDto, agenciaExistente);

        // Paso C: Guardar
        // Usamos refEq o el mismo objeto. Como el mapper es mock y no hace nada real,
        // agenciaExistente sigue teniendo el nombre viejo en memoria, pero para el test nos vale
        // validar que se llama al save con ese objeto.
        when(agenciaRepository.save(agenciaExistente)).thenReturn(agenciaGuardada);

        // Paso D: Convertir resultado a OutDto
        when(modelMapper.map(agenciaGuardada, AgenciaOutDto.class)).thenReturn(outDto);

        // 6. Ejecución
        AgenciaOutDto resultado = agenciaService.modify(id, inDto);

        // 7. Aserciones
        assertEquals("Nombre Modificado", resultado.getNombre());

        // Verificamos el flujo completo
        verify(agenciaRepository).findById(id);
        verify(modelMapper).map(inDto, agenciaExistente); // Verificamos explícitamente la llamada que fallaba
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

    // ----------------------------------------------------------------
    // TEST DELETE (Tu método busca primero y luego borra la entidad)
    // ----------------------------------------------------------------
    @Test
    public void testDelete() throws AgenciaNotFoundException {
        Long id = 1L;
        Agencia agenciaParaBorrar = new Agencia(id, "A Borrar", "C/ Borrar", 0f, 0, false, LocalDate.now(), null);

        // 1. Mocking: Tu servicio usa findById, NO existsById
        when(agenciaRepository.findById(id)).thenReturn(Optional.of(agenciaParaBorrar));

        // 2. Ejecución
        agenciaService.delete(id);

        // 3. Verificación
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
}