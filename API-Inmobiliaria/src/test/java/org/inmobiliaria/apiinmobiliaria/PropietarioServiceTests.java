package org.inmobiliaria.apiinmobiliaria;

import org.inmobiliaria.apiinmobiliaria.domain.Propietario;
import org.inmobiliaria.apiinmobiliaria.dto.PropietarioInDto;
import org.inmobiliaria.apiinmobiliaria.dto.PropietarioOutDto;
import org.inmobiliaria.apiinmobiliaria.exception.PropietarioNotFoundException;
import org.inmobiliaria.apiinmobiliaria.repository.PropietarioRepository;
import org.inmobiliaria.apiinmobiliaria.service.PropietarioService;
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
public class PropietarioServiceTests {

    @InjectMocks
    private PropietarioService propietarioService;

    @Mock
    private PropietarioRepository propietarioRepository;

    @Mock
    private ModelMapper modelMapper;


    // TEST FIND ALL (Sin filtros)
    @Test
    public void testFindAll() {
        // Entities
        List<Propietario> mockLista = List.of(
                new Propietario(1L, "123A", "Juan", "600111", 5.0f, false, LocalDate.now(), null),
                new Propietario(2L, "456B", "Empresa SL", "600222", 10.0f, true, LocalDate.now(), null)
        );

        // DTOs
        List<PropietarioOutDto> mockOutDtos = List.of(
                new PropietarioOutDto(1L, "123A", "Juan", "600111", 5.0f, false, LocalDate.now()),
                new PropietarioOutDto(2L, "456B", "Empresa SL", "600222", 10.0f, true, LocalDate.now())
        );

        // Mocking
        when(propietarioRepository.findAll()).thenReturn(mockLista);
        when(modelMapper.map(mockLista, new TypeToken<List<PropietarioOutDto>>() {}.getType()))
                .thenReturn(mockOutDtos);

        // Ejecución
        List<PropietarioOutDto> resultado = propietarioService.findAll(null, null, null);

        // Aserciones
        assertEquals(2, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());

        verify(propietarioRepository).findAll();
    }

    // TEST FIND ALL (Con filtros)
    @Test
    public void testFindAllWithFilters() {
        List<Propietario> mockLista = List.of(
                new Propietario(1L, "123A", "Juan", "600111", 5.0f, false, LocalDate.now(), null)
        );
        List<PropietarioOutDto> mockOutDtos = List.of(
                new PropietarioOutDto(1L, "123A", "Juan", "600111", 5.0f, false, LocalDate.now())
        );

        String dni = "123";
        String nombre = "Juan";
        Boolean esEmpresa = false;

        when(propietarioRepository.findByFilter(dni, nombre, esEmpresa))
                .thenReturn(mockLista);

        when(modelMapper.map(mockLista, new TypeToken<List<PropietarioOutDto>>() {}.getType()))
                .thenReturn(mockOutDtos);

        List<PropietarioOutDto> resultado = propietarioService.findAll(dni, nombre, esEmpresa);

        assertEquals(1, resultado.size());
        verify(propietarioRepository).findByFilter(dni, nombre, esEmpresa);
    }

    // TEST FIND BY ID
    @Test
    public void testFindById() throws PropietarioNotFoundException {
        Propietario mockEntity = new Propietario(1L, "123A", "Juan", "600", 5f, false, LocalDate.now(), null);
        PropietarioOutDto mockDto = new PropietarioOutDto(1L, "123A", "Juan", "600", 5f, false, LocalDate.now());

        when(propietarioRepository.findById(1L)).thenReturn(Optional.of(mockEntity));
        when(modelMapper.map(mockEntity, PropietarioOutDto.class)).thenReturn(mockDto);

        PropietarioOutDto result = propietarioService.findById(1L);

        assertEquals("Juan", result.getNombre());
    }

    @Test
    public void testFindByIdNotFound() {
        when(propietarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PropietarioNotFoundException.class, () -> {
            propietarioService.findById(99L);
        });
    }

    // TEST ADD (Entity -> Entity)
    @Test
    public void testAdd() {
        // ID 0L porque es primitivo long y entrada nueva
        Propietario input = new Propietario(0L, "111A", "Nuevo", "666", 3f, false, LocalDate.now(), null);
        Propietario guardado = new Propietario(1L, "111A", "Nuevo", "666", 3f, false, LocalDate.now(), null);

        // No usamos mapper aquí porque el servicio recibe y devuelve entidad directamente
        when(propietarioRepository.save(input)).thenReturn(guardado);

        Propietario resultado = propietarioService.add(input);

        assertEquals(1L, resultado.getId());
        assertEquals("Nuevo", resultado.getNombre());

        verify(propietarioRepository).save(input);
    }

    // TEST MODIFY (DTO -> DTO)
    @Test
    public void testModify() throws PropietarioNotFoundException {
        Long id = 1L;
        PropietarioInDto inDto = new PropietarioInDto("222B", "Editado", "777", 4f, true, LocalDate.now());

        Propietario existente = new Propietario(id, "111A", "Viejo", "666", 3f, false, LocalDate.now(), null);
        Propietario guardado = new Propietario(id, "222B", "Editado", "777", 4f, true, LocalDate.now(), null);
        PropietarioOutDto outDto = new PropietarioOutDto(id, "222B", "Editado", "777", 4f, true, LocalDate.now());

        when(propietarioRepository.findById(id)).thenReturn(Optional.of(existente));

        // Evitamos Strict Stubbing error
        doNothing().when(modelMapper).map(inDto, existente);

        when(propietarioRepository.save(existente)).thenReturn(guardado);
        when(modelMapper.map(guardado, PropietarioOutDto.class)).thenReturn(outDto);

        PropietarioOutDto result = propietarioService.modify(id, inDto);

        assertEquals("Editado", result.getNombre());
        verify(propietarioRepository).findById(id);
        verify(propietarioRepository).save(existente);
    }

    @Test
    public void testModifyNotFound() {
        Long id = 99L;
        PropietarioInDto inDto = new PropietarioInDto();

        when(propietarioRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PropietarioNotFoundException.class, () -> {
            propietarioService.modify(id, inDto);
        });

        verify(propietarioRepository, never()).save(any());
    }

    // TEST DELETE
    @Test
    public void testDelete() throws PropietarioNotFoundException {
        Long id = 1L;
        Propietario aBorrar = new Propietario(id, "x", "x", "x", 0f, false, LocalDate.now(), null);

        when(propietarioRepository.findById(id)).thenReturn(Optional.of(aBorrar));

        propietarioService.delete(id);

        verify(propietarioRepository).findById(id);
        verify(propietarioRepository).delete(aBorrar);
    }

    @Test
    public void testDeleteNotFound() {
        Long id = 99L;

        when(propietarioRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PropietarioNotFoundException.class, () -> {
            propietarioService.delete(id);
        });

        verify(propietarioRepository, never()).delete(any());
    }

    //TEST SQL GET EMPRESAS
    @Test
    public void testGetEmpresasSql() {
        List<Propietario> mockList = List.of(
                new Propietario(1L, "B123", "Empresa SL", "911", 10f, true, LocalDate.now(), null)

        );

        List<PropietarioOutDto> mockOut = List.of(
                new PropietarioOutDto(1L, "B123", "Empresa SL", "911", 10f, true, LocalDate.now())
        );

        when(propietarioRepository.findEmpresasNativas()).thenReturn(mockList);
        when(modelMapper.map(mockList, new TypeToken<List<PropietarioOutDto>>() {}.getType()))
                .thenReturn(mockOut);

        List<PropietarioOutDto> result = propietarioService.getEmpresasSql();

        assertEquals(1, result.size());
        assertEquals("Empresa SL", result.get(0).getNombre());

        verify(propietarioRepository, times(1)).findEmpresasNativas();
    }
}