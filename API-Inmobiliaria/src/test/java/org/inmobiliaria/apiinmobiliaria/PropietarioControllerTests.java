package org.inmobiliaria.apiinmobiliaria;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.inmobiliaria.apiinmobiliaria.controller.PropietarioController;
import org.inmobiliaria.apiinmobiliaria.domain.Propietario;
import org.inmobiliaria.apiinmobiliaria.dto.PropietarioInDto;
import org.inmobiliaria.apiinmobiliaria.dto.PropietarioOutDto;
import org.inmobiliaria.apiinmobiliaria.exception.PropietarioNotFoundException;
import org.inmobiliaria.apiinmobiliaria.service.PropietarioService;
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

@WebMvcTest(PropietarioController.class)
public class PropietarioControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PropietarioService propietarioService;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    // GET ALL (200 OK) - Sin filtros
    @Test
    public void testGetAll() throws Exception {
        // Datos simulados
        List<PropietarioOutDto> mockList = List.of(
                new PropietarioOutDto(1L, "123A", "Juan", "600111", 5f, false, LocalDate.now()),
                new PropietarioOutDto(2L, "456B", "Ana", "600222", 10f, true, LocalDate.now())
        );

        // Mock del servicio (sin filtros)
        when(propietarioService.findAll(null, null, null)).thenReturn(mockList);

        // Ejecución
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/propietarios")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Verificación
        String jsonResponse = result.getResponse().getContentAsString();
        List<PropietarioOutDto> responseList = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(2, responseList.size());
        assertEquals("Juan", responseList.get(0).getNombre());
    }

    // GET ALL (200 OK) - Con filtros
    @Test
    public void testGetAllWithFilters() throws Exception {
        // Datos de filtro
        String dni = "123";
        String nombre = "Juan";
        Boolean esEmpresa = false;

        // Respuesta simulada
        List<PropietarioOutDto> filteredList = List.of(
                new PropietarioOutDto(1L, "123A", "Juan", "600111", 5f, false, LocalDate.now())
        );

        // Mock
        when(propietarioService.findAll(dni, nombre, esEmpresa)).thenReturn(filteredList);

        // Ejecución
        mockMvc.perform(MockMvcRequestBuilders.get("/propietarios")
                        .param("dni", dni)
                        .param("nombre", nombre)
                        .param("esEmpresa", String.valueOf(esEmpresa))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].dni").value("123A"));
    }

    // GET BY ID (200 OK)
    @Test
    public void testGetById() throws Exception {
        PropietarioOutDto dto = new PropietarioOutDto(1L, "123A", "Juan", "600", 5f, false, LocalDate.now());

        when(propietarioService.findById(1L)).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/propietarios/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.id").value(1));
    }

    // GET BY ID (404 NOT FOUND)
    @Test
    public void testGetByIdNotFound() throws Exception {
        when(propietarioService.findById(99L)).thenThrow(new PropietarioNotFoundException("No existe"));

        mockMvc.perform(MockMvcRequestBuilders.get("/propietarios/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // POST (201 CREATED)
    @Test
    public void testAddPropietario() throws Exception {
        // Input DTO
        PropietarioInDto inDto = new PropietarioInDto("NEW", "Nuevo", "666", 5f, false, LocalDate.now());

        // Mocks intermedios
        Propietario entity = new Propietario(); entity.setNombre("Nuevo");
        Propietario savedEntity = new Propietario(); savedEntity.setId(1L); savedEntity.setNombre("Nuevo");
        PropietarioOutDto outDto = new PropietarioOutDto(1L, "NEW", "Nuevo", "666", 5f, false, LocalDate.now());

        when(modelMapper.map(any(PropietarioInDto.class), eq(Propietario.class))).thenReturn(entity);
        when(propietarioService.add(any(Propietario.class))).thenReturn(savedEntity);
        when(modelMapper.map(any(Propietario.class), eq(PropietarioOutDto.class))).thenReturn(outDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/propietarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Nuevo"))
                .andExpect(jsonPath("$.id").value(1));
    }

    // POST (400 BAD REQUEST)
    @Test
    public void testAddPropietarioBadRequest() throws Exception {
        // DTO inválido (nombre y dni vacíos)
        PropietarioInDto invalidDto = new PropietarioInDto("", "", "", null, null, null);

        mockMvc.perform(MockMvcRequestBuilders.post("/propietarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    // PUT (200 OK)
    @Test
    public void testModifyPropietario() throws Exception {
        Long id = 1L;
        PropietarioInDto inDto = new PropietarioInDto("EDIT", "Editado", "666", 5f, false, LocalDate.now());
        PropietarioOutDto outDto = new PropietarioOutDto(id, "EDIT", "Editado", "666", 5f, false, LocalDate.now());

        // En el PUT, tu controller pasa el DTO directo al servicio
        when(propietarioService.modify(eq(id), any(PropietarioInDto.class))).thenReturn(outDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/propietarios/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Editado"));
    }

    // PUT (404 NOT FOUND)
    @Test
    public void testModifyPropietarioNotFound() throws Exception {
        Long id = 99L;
        PropietarioInDto inDto = new PropietarioInDto("X", "X", "X", 1f, false, LocalDate.now());

        when(propietarioService.modify(eq(id), any(PropietarioInDto.class)))
                .thenThrow(new PropietarioNotFoundException("No existe"));

        mockMvc.perform(MockMvcRequestBuilders.put("/propietarios/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isNotFound());
    }

    // PUT (400 BAD REQUEST)
    @Test
    public void testModifyPropietarioBadRequest() throws Exception {
        Long id = 1L;
        // DTO Inválido (DNI vacío)
        PropietarioInDto invalidDto = new PropietarioInDto("", "Nombre", "666", 5f, false, null);

        mockMvc.perform(MockMvcRequestBuilders.put("/propietarios/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    // DELETE (204 NO CONTENT)
    @Test
    public void testDeletePropietario() throws Exception {
        Long id = 1L;
        doNothing().when(propietarioService).delete(id);

        mockMvc.perform(MockMvcRequestBuilders.delete("/propietarios/" + id))
                .andExpect(status().isNoContent());
    }

    // DELETE (404 NOT FOUND)
    @Test
    public void testDeletePropietarioNotFound() throws Exception {
        Long id = 99L;
        doThrow(new PropietarioNotFoundException("No existe")).when(propietarioService).delete(id);

        mockMvc.perform(MockMvcRequestBuilders.delete("/propietarios/" + id))
                .andExpect(status().isNotFound());
    }

    //SQL GET BY PROPIETARIO ES EMPRESA
    @Test
    public void testGetEmpresasSql() throws Exception {
        List<PropietarioOutDto> mockList = List.of(
                new PropietarioOutDto(1L, "B123", "Empresa SL", "911", 10f, true, LocalDate.now())
        );

        when(propietarioService.getEmpresasSql()).thenReturn(mockList);

        mockMvc.perform(MockMvcRequestBuilders.get("/propietarios/empresas")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].esEmpresa").value(true));
    }
}