package org.inmobiliaria.apiinmobiliaria;

import org.inmobiliaria.apiinmobiliaria.domain.Cliente;
import org.inmobiliaria.apiinmobiliaria.dto.ClienteInDto;
import org.inmobiliaria.apiinmobiliaria.dto.ClienteOutDto;
import org.inmobiliaria.apiinmobiliaria.exception.ClienteNotFoundException; // Asegúrate de tener esta excepción creada
import org.inmobiliaria.apiinmobiliaria.repository.ClienteRepository;
import org.inmobiliaria.apiinmobiliaria.service.ClienteService;
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
public class ClienteServiceTests {

    @InjectMocks
    private ClienteService clienteService;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ModelMapper modelMapper;

    // ----------------------------------------------------------------
    // TEST FIND ALL (Sin filtros)
    // ----------------------------------------------------------------
    @Test
    public void testFindAll() {
        // 1. Datos simulados (Entities)
        // Constructor Cliente: id, email, password, telefono, presupuesto, edad, fechaAlta, suscrito, visitas
        List<Cliente> mockLista = List.of(
                new Cliente(1L, "ana@mail.com", "1234", "600111222", 150000f, 30, LocalDate.now(), true, null),
                new Cliente(2L, "bob@mail.com", "5678", "600333444", 200000f, 40, LocalDate.now(), false, null)
        );

        // 2. Respuesta esperada (DTOs de salida)
        // Constructor OutDto: id, email, telefono, presupuesto, edad, suscrito
        List<ClienteOutDto> mockOutDtos = List.of(
                new ClienteOutDto(1L, "ana@mail.com", "600111222", 150000f, 30, true),
                new ClienteOutDto(2L, "bob@mail.com", "600333444", 200000f, 40, false)
        );

        // 3. Mocking
        when(clienteRepository.findAll()).thenReturn(mockLista);
        when(modelMapper.map(mockLista, new TypeToken<List<ClienteOutDto>>() {}.getType())).thenReturn(mockOutDtos);

        // 4. Ejecución (Pasamos nulls asumiendo que tu servicio recibe 3 filtros, ajusta si son diferentes)
        List<ClienteOutDto> resultado = clienteService.findAll(null, null, null);

        // 5. Aserciones
        assertEquals(2, resultado.size());
        assertEquals("ana@mail.com", resultado.get(0).getEmail());

        verify(clienteRepository, times(1)).findAll();
    }

    // ----------------------------------------------------------------
    // TEST FIND ALL (Con filtros)
    // ----------------------------------------------------------------
    @Test
    public void testFindAllWithFilters() {
        // 1. Entity resultante
        List<Cliente> mockLista = List.of(
                new Cliente(1L, "ana@mail.com", "1234", "600111222", 150000f, 30, LocalDate.now(), true, null)
        );

        // 2. DTO resultante
        List<ClienteOutDto> mockOutDtos = List.of(
                new ClienteOutDto(1L, "ana@mail.com", "600111222", 150000f, 30, true)
        );

        // 3. Filtros
        String email = "ana";
        String telefono = "600";
        Boolean suscrito = true;

        // 4. Mocking
        // Asumo que tu repositorio tiene un método findBy similar a este. Ajusta el nombre si es distinto.
        when(clienteRepository.findByEmailContainingAndTelefonoContainingAndSuscrito(email, telefono, suscrito))
                .thenReturn(mockLista);
        when(modelMapper.map(mockLista, new TypeToken<List<ClienteOutDto>>() {}.getType())).thenReturn(mockOutDtos);

        // 5. Ejecución
        List<ClienteOutDto> resultado = clienteService.findAll(email, telefono, suscrito);

        // 6. Aserciones
        assertEquals(1, resultado.size());
        verify(clienteRepository, times(0)).findAll(); // Aseguramos que NO llama al genérico
        verify(clienteRepository, times(1)).findByEmailContainingAndTelefonoContainingAndSuscrito(email, telefono, suscrito);
    }

    // ----------------------------------------------------------------
    // TEST FIND BY ID
    // ----------------------------------------------------------------
    @Test
    public void testFindById() throws ClienteNotFoundException {
        // Entity
        Cliente mockCliente = new Cliente(1L, "ana@mail.com", "1234", "600111222", 150000f, 30, LocalDate.now(), true, null);
        // DTO Salida
        ClienteOutDto mockOutDto = new ClienteOutDto(1L, "ana@mail.com", "600111222", 150000f, 30, true);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(mockCliente));
        when(modelMapper.map(mockCliente, ClienteOutDto.class)).thenReturn(mockOutDto);

        ClienteOutDto result = clienteService.findById(1L);

        assertEquals("ana@mail.com", result.getEmail());
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindByIdNotFound() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ClienteNotFoundException.class, () -> {
            clienteService.findById(99L);
        });

        verify(clienteRepository, times(1)).findById(99L);
    }

    // ----------------------------------------------------------------
    // TEST ADD (Entity -> Entity, ID primitivo 0L)
    // ----------------------------------------------------------------
    @Test
    public void testAdd() {
        // 1. Datos entrada (ID 0L obligatoriamente porque es long primitivo)
        Cliente input = new Cliente(0L, "nuevo@mail.com", "pass", "666777888", 100f, 25, LocalDate.now(), false, null);

        // 2. Datos guardados (ID 1L generado)
        Cliente guardado = new Cliente(1L, "nuevo@mail.com", "pass", "666777888", 100f, 25, LocalDate.now(), false, null);

        // 3. Mocking
        when(clienteRepository.save(input)).thenReturn(guardado);

        // 4. Ejecución
        Cliente resultado = clienteService.add(input);

        // 5. Aserciones
        assertEquals(1L, resultado.getId());
        assertEquals("nuevo@mail.com", resultado.getEmail());

        verify(clienteRepository, times(1)).save(input);
    }

    // ----------------------------------------------------------------
    // TEST MODIFY (DTO -> DTO, con corrección de Stubbing)
    // ----------------------------------------------------------------
    @Test
    public void testModify() throws ClienteNotFoundException {
        Long id = 1L;

        // 1. DTO Entrada (Datos nuevos)
        ClienteInDto inDto = new ClienteInDto("modificado@mail.com", "newPass", "999888777", 300f, 35, LocalDate.now(), true);

        // 2. Entity existente (Datos viejos)
        Cliente existente = new Cliente(id, "viejo@mail.com", "oldPass", "000000000", 100f, 20, LocalDate.of(2000,1,1), false, null);

        // 3. Entity guardada (Datos nuevos + ID asegurado)
        Cliente guardado = new Cliente(id, "modificado@mail.com", "newPass", "999888777", 300f, 35, LocalDate.now(), true, null);

        // 4. DTO Salida Esperado
        ClienteOutDto outDto = new ClienteOutDto(id, "modificado@mail.com", "999888777", 300f, 35, true);

        // 5. Mocking
        when(clienteRepository.findById(id)).thenReturn(Optional.of(existente));

        // CRUCIAL: Evitar error Strict Stubbing de Mockito
        doNothing().when(modelMapper).map(inDto, existente);

        when(clienteRepository.save(existente)).thenReturn(guardado);
        when(modelMapper.map(guardado, ClienteOutDto.class)).thenReturn(outDto);

        // 6. Ejecución
        ClienteOutDto resultado = clienteService.modify(id, inDto);

        // 7. Aserciones
        assertEquals("modificado@mail.com", resultado.getEmail());
        assertEquals(300f, resultado.getPresupuestoMaximo());

        verify(clienteRepository).findById(id);
        verify(modelMapper).map(inDto, existente);
        verify(clienteRepository).save(existente);
    }

    @Test
    public void testModifyNotFound() {
        Long id = 99L;
        ClienteInDto inDto = new ClienteInDto(); // datos irrelevantes

        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ClienteNotFoundException.class, () -> {
            clienteService.modify(id, inDto);
        });

        verify(clienteRepository, never()).save(any());
    }

    // ----------------------------------------------------------------
    // TEST DELETE (Busca -> Borra objeto)
    // ----------------------------------------------------------------
    @Test
    public void testDelete() throws ClienteNotFoundException {
        Long id = 1L;
        Cliente clienteABorrar = new Cliente(id, "a@borrar.com", "x", "x", 0f, 18, LocalDate.now(), false, null);

        // 1. Mocking: FindById porque tu servicio recupera antes de borrar
        when(clienteRepository.findById(id)).thenReturn(Optional.of(clienteABorrar));

        // 2. Ejecución
        clienteService.delete(id);

        // 3. Verificación
        verify(clienteRepository, times(1)).findById(id);
        verify(clienteRepository, times(1)).delete(clienteABorrar);
    }

    @Test
    public void testDeleteNotFound() {
        Long id = 99L;

        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ClienteNotFoundException.class, () -> {
            clienteService.delete(id);
        });

        verify(clienteRepository, never()).delete(any());
    }
}