package org.inmobiliaria.apiinmobiliaria;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.inmobiliaria.apiinmobiliaria.controller.ClienteController;
import org.inmobiliaria.apiinmobiliaria.domain.Cliente;
import org.inmobiliaria.apiinmobiliaria.dto.ClienteInDto;
import org.inmobiliaria.apiinmobiliaria.dto.ClienteOutDto;
import org.inmobiliaria.apiinmobiliaria.exception.ClienteNotFoundException;
import org.inmobiliaria.apiinmobiliaria.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClienteController.class)
public class ClienteControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;


    // GET ALL (200 OK) - Sin filtros
    @Test
    public void testGetAll() throws Exception {
        // Datos simulados
        List<ClienteOutDto> mockList = List.of(
                new ClienteOutDto(1L, "ana@test.com", "600111222", 150000f, 30, true),
                new ClienteOutDto(2L, "bob@test.com", "600333444", 200000f, 40, false)
        );

        // Mock del servicio (filtros nulos)
        when(clienteService.findAll(null, null, null)).thenReturn(mockList);

        // Ejecución
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/clientes")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Verificación
        String jsonResponse = result.getResponse().getContentAsString();
        List<ClienteOutDto> responseList = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(2, responseList.size());
        assertEquals("ana@test.com", responseList.get(0).getEmail());
    }

    //GET ALL (200 OK) - Con filtros
    @Test
    public void testGetAllWithFilters() throws Exception {
        // Datos de filtro
        String email = "ana";
        String telefono = "600";
        Boolean suscrito = true;

        // Respuesta simulada
        List<ClienteOutDto> filteredList = List.of(
                new ClienteOutDto(1L, "ana@test.com", "600111222", 150000f, 30, true)
        );

        // Mock
        when(clienteService.findAll(email, telefono, suscrito)).thenReturn(filteredList);

        // Ejecución con query params
        mockMvc.perform(MockMvcRequestBuilders.get("/clientes")
                        .param("email", email)
                        .param("telefono", telefono)
                        .param("suscrito", String.valueOf(suscrito))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].email").value("ana@test.com"));
    }

    // GET BY ID (200 OK)
    @Test
    public void testGetById() throws Exception {
        ClienteOutDto dto = new ClienteOutDto(1L, "ana@test.com", "600111222", 150000f, 30, true);

        when(clienteService.findById(1L)).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/clientes/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ana@test.com"))
                .andExpect(jsonPath("$.id").value(1));
    }

    // GET BY ID (404 NOT FOUND)
    @Test
    public void testGetByIdNotFound() throws Exception {
        when(clienteService.findById(99L)).thenThrow(new ClienteNotFoundException("No existe"));

        mockMvc.perform(MockMvcRequestBuilders.get("/clientes/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // POST (201 CREATED)
    @Test
    public void testAddCliente() throws Exception {
        // Datos entrada (Debe ser válido para pasar @Valid)
        ClienteInDto inDto = new ClienteInDto("new@test.com", "1234", "666777888", 100f, 25, LocalDate.now(), true);

        // Entidades intermedias y finales
        Cliente clienteEntity = new Cliente(); clienteEntity.setEmail("new@test.com");
        Cliente clienteGuardado = new Cliente(); clienteGuardado.setId(1L); clienteGuardado.setEmail("new@test.com");
        ClienteOutDto outDto = new ClienteOutDto(1L, "new@test.com", "666777888", 100f, 25, true);

        // Mocking: Dto -> Entity -> Service -> Entity -> Dto
        when(modelMapper.map(any(ClienteInDto.class), eq(Cliente.class))).thenReturn(clienteEntity);
        when(clienteService.add(any(Cliente.class))).thenReturn(clienteGuardado);
        when(modelMapper.map(any(Cliente.class), eq(ClienteOutDto.class))).thenReturn(outDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new@test.com"))
                .andExpect(jsonPath("$.id").value(1));
    }

    // POST (400 BAD REQUEST)
    @Test
    public void testAddClienteBadRequest() throws Exception {
        // Email inválido para disparar validación
        ClienteInDto invalidDto = new ClienteInDto("email-invalido", "", "", null, null, null, null);

        mockMvc.perform(MockMvcRequestBuilders.post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    // PUT (200 OK)
    @Test
    public void testModifyCliente() throws Exception {
        Long id = 1L;
        // Dto válido
        ClienteInDto inDto = new ClienteInDto("edit@test.com", "1234", "666", 200f, 30, LocalDate.now(), false);
        ClienteOutDto outDto = new ClienteOutDto(id, "edit@test.com", "666", 200f, 30, false);

        when(clienteService.modify(eq(id), any(ClienteInDto.class))).thenReturn(outDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/clientes/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("edit@test.com"));
    }

    // PUT (404 NOT FOUND)
    @Test
    public void testModifyClienteNotFound() throws Exception {
        Long id = 99L;
        ClienteInDto inDto = new ClienteInDto("x@x.com", "x", "x", 1f, 18, LocalDate.now(), true);

        when(clienteService.modify(eq(id), any(ClienteInDto.class)))
                .thenThrow(new ClienteNotFoundException("No existe"));

        mockMvc.perform(MockMvcRequestBuilders.put("/clientes/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isNotFound());
    }

    // PUT (400 BAD REQUEST)
    @Test
    public void testModifyClienteBadRequest() throws Exception {
        Long id = 1L;
        // DTO inválido (email incorrecto)
        ClienteInDto invalidDto = new ClienteInDto("no-es-email", "pass", "123", 100f, 20, null, false);

        mockMvc.perform(MockMvcRequestBuilders.put("/clientes/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    // DELETE (204 NO CONTENT)
    @Test
    public void testDeleteCliente() throws Exception {
        Long id = 1L;
        doNothing().when(clienteService).delete(id);

        mockMvc.perform(MockMvcRequestBuilders.delete("/clientes/" + id))
                .andExpect(status().isNoContent());
    }

    // DELETE (404 NOT FOUND)
    @Test
    public void testDeleteClienteNotFound() throws Exception {
        Long id = 99L;
        doThrow(new ClienteNotFoundException("No existe")).when(clienteService).delete(id);

        mockMvc.perform(MockMvcRequestBuilders.delete("/clientes/" + id))
                .andExpect(status().isNotFound());
    }
}