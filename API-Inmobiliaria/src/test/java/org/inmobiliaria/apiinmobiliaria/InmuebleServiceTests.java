package org.inmobiliaria.apiinmobiliaria;

import org.inmobiliaria.apiinmobiliaria.domain.Agencia;
import org.inmobiliaria.apiinmobiliaria.domain.Inmueble;
import org.inmobiliaria.apiinmobiliaria.domain.Propietario;
import org.inmobiliaria.apiinmobiliaria.dto.InmuebleInDto;
import org.inmobiliaria.apiinmobiliaria.dto.InmuebleOutDto;
import org.inmobiliaria.apiinmobiliaria.exception.AgenciaNotFoundException;
import org.inmobiliaria.apiinmobiliaria.exception.InmuebleNotFoundException;
import org.inmobiliaria.apiinmobiliaria.exception.PropietarioNotFoundException;
import org.inmobiliaria.apiinmobiliaria.repository.AgenciaRepository;
import org.inmobiliaria.apiinmobiliaria.repository.InmuebleRepository;
import org.inmobiliaria.apiinmobiliaria.repository.PropietarioRepository;
import org.inmobiliaria.apiinmobiliaria.service.InmuebleService;
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
public class InmuebleServiceTests {

    @InjectMocks
    private InmuebleService inmuebleService;

    @Mock
    private InmuebleRepository inmuebleRepository;

    @Mock
    private AgenciaRepository agenciaRepository; // Necesario para ADD

    @Mock
    private PropietarioRepository propietarioRepository; // Necesario para ADD

    @Mock
    private ModelMapper modelMapper;

    // ----------------------------------------------------------------
    // TEST FIND ALL (Sin filtros)
    // ----------------------------------------------------------------
    @Test
    public void testFindAll() {
        // Datos simulados
        List<Inmueble> mockLista = List.of(
                new Inmueble(1L, "Piso Centro", 100000f, 80, 0d, 0d, true, LocalDate.now(), null, null, null)
        );
        List<InmuebleOutDto> mockOutDtos = List.of(
                new InmuebleOutDto(1L, "Piso Centro", 100000f, 80, 0d, 0d, true, LocalDate.now(), 1L, 1L)
        );

        when(inmuebleRepository.findAll()).thenReturn(mockLista);
        when(modelMapper.map(mockLista, new TypeToken<List<InmuebleOutDto>>() {}.getType())).thenReturn(mockOutDtos);

        List<InmuebleOutDto> resultado = inmuebleService.findAll(null, null, null);

        assertEquals(1, resultado.size());
        verify(inmuebleRepository).findAll();
    }

    // ----------------------------------------------------------------
    // TEST FIND ALL (Con filtros: Precio, Metros, Ascensor)
    // ----------------------------------------------------------------
    @Test
    public void testFindAllWithFilters() {
        List<Inmueble> mockLista = List.of(new Inmueble());
        List<InmuebleOutDto> mockOutDtos = List.of(new InmuebleOutDto());

        Float precioMax = 200000f;
        Integer metrosMin = 90;
        Boolean ascensor = true;

        // Asegúrate de que este método coincida con tu Repo
        when(inmuebleRepository.findByPrecioLessThanEqualAndMetrosGreaterThanEqualAndAscensor(precioMax, metrosMin, ascensor))
                .thenReturn(mockLista);

        when(modelMapper.map(mockLista, new TypeToken<List<InmuebleOutDto>>() {}.getType()))
                .thenReturn(mockOutDtos);

        List<InmuebleOutDto> resultado = inmuebleService.findAll(precioMax, metrosMin, ascensor);

        assertEquals(1, resultado.size());
        verify(inmuebleRepository).findByPrecioLessThanEqualAndMetrosGreaterThanEqualAndAscensor(precioMax, metrosMin, ascensor);
    }

    // ----------------------------------------------------------------
    // TEST FIND BY ID
    // ----------------------------------------------------------------
    @Test
    public void testFindById() throws InmuebleNotFoundException {
        Inmueble mockInmueble = new Inmueble(1L, "Piso", 100f, 50, 0d, 0d, false, LocalDate.now(), null, null, null);
        InmuebleOutDto mockOutDto = new InmuebleOutDto(1L, "Piso", 100f, 50, 0d, 0d, false, LocalDate.now(), 1L, 1L);

        when(inmuebleRepository.findById(1L)).thenReturn(Optional.of(mockInmueble));
        when(modelMapper.map(mockInmueble, InmuebleOutDto.class)).thenReturn(mockOutDto);

        InmuebleOutDto result = inmuebleService.findById(1L);

        assertEquals("Piso", result.getTitulo());
    }

    // ----------------------------------------------------------------
    // TEST FIND BY ID - NOT FOUND
    // ----------------------------------------------------------------
    @Test
    public void testFindByIdNotFound() {
        Long id = 99L; // Un ID que no existe

        // Simulamos que el repositorio devuelve vacío
        when(inmuebleRepository.findById(id)).thenReturn(Optional.empty());

        // Verificamos que salta la excepción específica
        assertThrows(InmuebleNotFoundException.class, () -> {
            inmuebleService.findById(id);
        });

        // Verificamos que se llamó al repositorio
        verify(inmuebleRepository).findById(id);
    }
    // ----------------------------------------------------------------
    // TEST ADD (DTO -> DTO + Relaciones)
    // ----------------------------------------------------------------
    @Test
    public void testAdd() {
        // 1. Datos entrada
        InmuebleInDto inDto = new InmuebleInDto("Nuevo Piso", 150000f, 90, 0d, 0d, true, LocalDate.now(), 10L, 20L);

        // 2. Mocks de relaciones
        Agencia mockAgencia = new Agencia(); mockAgencia.setId(10L);
        Propietario mockPropietario = new Propietario(); mockPropietario.setId(20L);

        // 3. Entidades
        Inmueble inmuebleMapeado = new Inmueble(); // Vacío tras new
        Inmueble inmuebleGuardado = new Inmueble(1L, "Nuevo Piso", 150000f, 90, 0d, 0d, true, LocalDate.now(), mockAgencia, mockPropietario, null);

        // 4. Salida esperada
        InmuebleOutDto outDto = new InmuebleOutDto(1L, "Nuevo Piso", 150000f, 90, 0d, 0d, true, LocalDate.now(), 10L, 20L);

        // 5. Configurar Mocks
        when(agenciaRepository.findById(10L)).thenReturn(Optional.of(mockAgencia));
        when(propietarioRepository.findById(20L)).thenReturn(Optional.of(mockPropietario));

        // Strict Stubbing: Tu servicio hace new Inmueble() y luego map(dto, inmueble)
        // Como 'inmueble' es una variable local dentro del método, Mockito no puede interceptarla fácilmente con 'eq()'.
        // Usamos any(Inmueble.class) para decirle "cuando mapees a cualquier inmueble, no hagas nada"
        doNothing().when(modelMapper).map(eq(inDto), any(Inmueble.class));

        when(inmuebleRepository.save(any(Inmueble.class))).thenReturn(inmuebleGuardado);
        when(modelMapper.map(inmuebleGuardado, InmuebleOutDto.class)).thenReturn(outDto);

        // 6. Ejecución
        InmuebleOutDto result = inmuebleService.add(inDto);

        // 7. Verificación
        assertEquals(1L, result.getId());
        verify(agenciaRepository).findById(10L);
        verify(propietarioRepository).findById(20L);
        verify(inmuebleRepository).save(any(Inmueble.class));
    }

    @Test
    public void testAddAgenciaNotFound() {
        InmuebleInDto inDto = new InmuebleInDto();
        inDto.setAgenciaId(99L);

        when(agenciaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(AgenciaNotFoundException.class, () -> inmuebleService.add(inDto));
    }

    // ----------------------------------------------------------------
    // TEST MODIFY (DTO -> DTO)
    // ----------------------------------------------------------------
    @Test
    public void testModify() throws InmuebleNotFoundException {
        Long id = 1L;
        InmuebleInDto inDto = new InmuebleInDto("Editado", 200000f, 100, 0d, 0d, false, LocalDate.now(), 10L, 20L);

        Inmueble existente = new Inmueble(id, "Viejo", 100f, 50, 0d, 0d, false, LocalDate.now(), null, null, null);
        Inmueble guardado = new Inmueble(id, "Editado", 200000f, 100, 0d, 0d, false, LocalDate.now(), null, null, null);
        InmuebleOutDto outDto = new InmuebleOutDto(id, "Editado", 200000f, 100, 0d, 0d, false, LocalDate.now(), 10L, 20L);

        when(inmuebleRepository.findById(id)).thenReturn(Optional.of(existente));

        // Evitar Strict Stubbing error
        doNothing().when(modelMapper).map(inDto, existente);

        when(inmuebleRepository.save(existente)).thenReturn(guardado);
        when(modelMapper.map(guardado, InmuebleOutDto.class)).thenReturn(outDto);

        InmuebleOutDto result = inmuebleService.modify(id, inDto);

        assertEquals("Editado", result.getTitulo());
        verify(inmuebleRepository).findById(id);
        verify(inmuebleRepository).save(existente);
    }

    // ----------------------------------------------------------------
    // TEST MODIFY - NOT FOUND
    // ----------------------------------------------------------------
    @Test
    public void testModifyNotFound() {
        Long id = 99L;
        InmuebleInDto inDto = new InmuebleInDto(); // Los datos dan igual porque va a fallar antes

        // Simulamos que no encuentra el inmueble
        when(inmuebleRepository.findById(id)).thenReturn(Optional.empty());

        // Ejecutamos y esperamos el error
        assertThrows(InmuebleNotFoundException.class, () -> {
            inmuebleService.modify(id, inDto);
        });

        // IMPORTANTE: Aseguramos que NUNCA se intentó guardar nada
        verify(inmuebleRepository, never()).save(any());
    }

    // ----------------------------------------------------------------
    // TEST DELETE
    // ----------------------------------------------------------------
    @Test
    public void testDelete() throws InmuebleNotFoundException {
        Long id = 1L;
        Inmueble aBorrar = new Inmueble(); aBorrar.setId(id);

        when(inmuebleRepository.findById(id)).thenReturn(Optional.of(aBorrar));

        inmuebleService.delete(id);

        verify(inmuebleRepository).delete(aBorrar);
    }

    @Test
    public void testDeleteNotFound() {
        Long id = 99L;
        when(inmuebleRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(InmuebleNotFoundException.class, () -> inmuebleService.delete(id));
    }
}