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


    // TEST FIND ALL (Sin filtros)
    @Test
    public void testFindAll() {
        // Datos simulados (Entities)
        // Constructor Cliente: id, email, password, telefono, presupuesto, edad, fechaAlta, suscrito, visitas
        List<Cliente> mockLista = List.of(
                new Cliente(1L, "ana@mail.com", "1234", "600111222", 150000f, 30, LocalDate.now(), true, null),
                new Cliente(2L, "bob@mail.com", "5678", "600333444", 200000f, 40, LocalDate.now(), false, null)
        );

        // Respuesta esperada (DTOs de salida)
        // Constructor OutDto: id, email, telefono, presupuesto, edad, suscrito
        List<ClienteOutDto> mockOutDtos = List.of(
                new ClienteOutDto(1L, "ana@mail.com", "600111222", 150000f, 30, true),
                new ClienteOutDto(2L, "bob@mail.com", "600333444", 200000f, 40, false)
        );

        // Mocking
        when(clienteRepository.findAll()).thenReturn(mockLista);
        when(modelMapper.map(mockLista, new TypeToken<List<ClienteOutDto>>() {}.getType())).thenReturn(mockOutDtos);

        // Ejecución (Pasamos nulls asumiendo que tu servicio recibe 3 filtros, ajusta si son diferentes)
        List<ClienteOutDto> resultado = clienteService.findAll(null, null, null);

        // Aserciones
        assertEquals(2, resultado.size());
        assertEquals("ana@mail.com", resultado.get(0).getEmail());

        verify(clienteRepository, times(1)).findAll();
    }

    // TEST FIND ALL (Con filtros)
    @Test
    public void testFindAllWithFilters() {
        // Entity resultante
        List<Cliente> mockLista = List.of(
                new Cliente(1L, "ana@mail.com", "1234", "600111222", 150000f, 30, LocalDate.now(), true, null)
        );

        // DTO resultante
        List<ClienteOutDto> mockOutDtos = List.of(
                new ClienteOutDto(1L, "ana@mail.com", "600111222", 150000f, 30, true)
        );

        // Filtros
        String email = "ana";
        String telefono = "600";
        Boolean suscrito = true;

        // Mocking
        when(clienteRepository.findByEmailContainingAndTelefonoContainingAndSuscrito(email, telefono, suscrito))
                .thenReturn(mockLista);
        when(modelMapper.map(mockLista, new TypeToken<List<ClienteOutDto>>() {}.getType())).thenReturn(mockOutDtos);

        // Ejecución
        List<ClienteOutDto> resultado = clienteService.findAll(email, telefono, suscrito);

        // serciones
        assertEquals(1, resultado.size());
        verify(clienteRepository, times(0)).findAll(); // Aseguramos que NO llama al genérico
        verify(clienteRepository, times(1)).findByEmailContainingAndTelefonoContainingAndSuscrito(email, telefono, suscrito);
    }

    // TEST FIND BY ID
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

    // TEST ADD (Entity -> Entity)
    @Test
    public void testAdd() {
        // Datos entrada (ID 0L obligatoriamente porque es long)
        Cliente input = new Cliente(0L, "nuevo@mail.com", "pass", "666777888", 100f, 25, LocalDate.now(), false, null);

        // Datos guardados (ID 1L generado)
        Cliente guardado = new Cliente(1L, "nuevo@mail.com", "pass", "666777888", 100f, 25, LocalDate.now(), false, null);

        // Mocking
        when(clienteRepository.save(input)).thenReturn(guardado);

        // Ejecución
        Cliente resultado = clienteService.add(input);

        // Aserciones
        assertEquals(1L, resultado.getId());
        assertEquals("nuevo@mail.com", resultado.getEmail());

        verify(clienteRepository, times(1)).save(input);
    }

    // TEST MODIFY
    @Test
    public void testModify() throws ClienteNotFoundException {
        Long id = 1L;

        // DTO Entrada (Datos nuevos)
        ClienteInDto inDto = new ClienteInDto("modificado@mail.com", "newPass", "999888777", 300f, 35, LocalDate.now(), true);

        // Entity existente (Datos viejos)
        Cliente existente = new Cliente(id, "viejo@mail.com", "oldPass", "000000000", 100f, 20, LocalDate.of(2000,1,1), false, null);

        // Entity guardada (Datos nuevos + ID asegurado)
        Cliente guardado = new Cliente(id, "modificado@mail.com", "newPass", "999888777", 300f, 35, LocalDate.now(), true, null);

        // DTO Salida Esperado
        ClienteOutDto outDto = new ClienteOutDto(id, "modificado@mail.com", "999888777", 300f, 35, true);

        // Mocking
        when(clienteRepository.findById(id)).thenReturn(Optional.of(existente));

        // Evitar error Strict
        doNothing().when(modelMapper).map(inDto, existente);

        when(clienteRepository.save(existente)).thenReturn(guardado);
        when(modelMapper.map(guardado, ClienteOutDto.class)).thenReturn(outDto);

        // Ejecución
        ClienteOutDto resultado = clienteService.modify(id, inDto);

        // Aserciones
        assertEquals("modificado@mail.com", resultado.getEmail());
        assertEquals(300f, resultado.getPresupuestoMaximo());

        verify(clienteRepository).findById(id);
        verify(modelMapper).map(inDto, existente);
        verify(clienteRepository).save(existente);
    }

    @Test
    public void testModifyNotFound() {
        Long id = 99L;
        ClienteInDto inDto = new ClienteInDto();

        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ClienteNotFoundException.class, () -> {
            clienteService.modify(id, inDto);
        });

        verify(clienteRepository, never()).save(any());
    }

    // TEST DELETE
    @Test
    public void testDelete() throws ClienteNotFoundException {
        Long id = 1L;
        Cliente clienteABorrar = new Cliente(id, "a@borrar.com", "x", "x", 0f, 18, LocalDate.now(), false, null);

        // Mocking: FindById porque el servicie recupera antes de borrar
        when(clienteRepository.findById(id)).thenReturn(Optional.of(clienteABorrar));

        // Ejecución
        clienteService.delete(id);

        // Verificación
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