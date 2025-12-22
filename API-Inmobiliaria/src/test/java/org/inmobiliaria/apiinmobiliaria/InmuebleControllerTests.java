package org.inmobiliaria.apiinmobiliaria;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.inmobiliaria.apiinmobiliaria.controller.InmuebleController;
import org.inmobiliaria.apiinmobiliaria.dto.InmuebleInDto;
import org.inmobiliaria.apiinmobiliaria.dto.InmuebleOutDto;
import org.inmobiliaria.apiinmobiliaria.exception.InmuebleNotFoundException;
import org.inmobiliaria.apiinmobiliaria.service.InmuebleService;
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

@WebMvcTest(InmuebleController.class)
public class InmuebleControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InmuebleService inmuebleService;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    // GET ALL (200 OK) - Sin filtros
    @Test
    public void testGetAll() throws Exception {
        // Datos simulados
        List<InmuebleOutDto> mockList = List.of(
                new InmuebleOutDto(1L, "Piso Centro", 100000f, 80, 0d, 0d, true, LocalDate.now(), 1L, 1L),
                new InmuebleOutDto(2L, "Chalet Afueras", 200000f, 150, 0d, 0d, false, LocalDate.now(), 1L, 1L)
        );

        // Mock del servicio (filtros nulos)
        when(inmuebleService.findAll(null, null, null)).thenReturn(mockList);

        // Ejecución
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/inmuebles")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Verificación
        String jsonResponse = result.getResponse().getContentAsString();
        List<InmuebleOutDto> responseList = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(2, responseList.size());
        assertEquals("Piso Centro", responseList.get(0).getTitulo());
    }

    // GET ALL (200 OK) - Con filtros
    @Test
    public void testGetAllWithFilters() throws Exception {
        // Datos de filtro
        Float precioMax = 150000f;
        Integer metrosMin = 70;
        Boolean ascensor = true;

        // Respuesta simulada
        List<InmuebleOutDto> filteredList = List.of(
                new InmuebleOutDto(1L, "Piso Barato", 100000f, 80, 0d, 0d, true, LocalDate.now(), 1L, 1L)
        );

        // Mock
        when(inmuebleService.findAll(precioMax, metrosMin, ascensor)).thenReturn(filteredList);

        // Ejecución con query params
        mockMvc.perform(MockMvcRequestBuilders.get("/inmuebles")
                        .param("precioMax", String.valueOf(precioMax))
                        .param("metrosMin", String.valueOf(metrosMin))
                        .param("ascensor", String.valueOf(ascensor))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Piso Barato"));
    }

    // GET BY ID (200 OK)
    @Test
    public void testGetById() throws Exception {
        InmuebleOutDto dto = new InmuebleOutDto(1L, "Piso Test", 100000f, 80, 0d, 0d, true, LocalDate.now(), 1L, 1L);

        when(inmuebleService.findById(1L)).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/inmuebles/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Piso Test"))
                .andExpect(jsonPath("$.id").value(1));
    }

    // GET BY ID (404 NOT FOUND)
    @Test
    public void testGetByIdNotFound() throws Exception {
        when(inmuebleService.findById(99L)).thenThrow(new InmuebleNotFoundException("No existe"));

        mockMvc.perform(MockMvcRequestBuilders.get("/inmuebles/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // POST (201 CREATED)
    @Test
    public void testAddInmueble() throws Exception {
        // Datos entrada (Validamos campos obligatorios: titulo, precio, metros, lat, lon, ascensor, fecha, agencia, prop)
        InmuebleInDto inDto = new InmuebleInDto("Nuevo Piso", 150000f, 90, 10d, 20d, true, LocalDate.now(), 1L, 2L);

        // DTO Salida
        InmuebleOutDto outDto = new InmuebleOutDto(1L, "Nuevo Piso", 150000f, 90, 10d, 20d, true, LocalDate.now(), 1L, 2L);

        // Mocking: En este controller, se pasa el DTO directo al servicio.
        when(inmuebleService.add(any(InmuebleInDto.class))).thenReturn(outDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/inmuebles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Nuevo Piso"))
                .andExpect(jsonPath("$.id").value(1));
    }

    // POST (400 BAD REQUEST)
    @Test
    public void testAddInmuebleBadRequest() throws Exception {
        // DTO inválido (Titulo vacío, Precio nulo)
        InmuebleInDto invalidDto = new InmuebleInDto("", null, null, null, null, null, null, null, null);

        mockMvc.perform(MockMvcRequestBuilders.post("/inmuebles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    // PUT (200 OK)
    @Test
    public void testModifyInmueble() throws Exception {
        Long id = 1L;
        InmuebleInDto inDto = new InmuebleInDto("Editado", 200000f, 100, 10d, 20d, false, LocalDate.now(), 1L, 2L);
        InmuebleOutDto outDto = new InmuebleOutDto(id, "Editado", 200000f, 100, 10d, 20d, false, LocalDate.now(), 1L, 2L);

        when(inmuebleService.modify(eq(id), any(InmuebleInDto.class))).thenReturn(outDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/inmuebles/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Editado"));
    }

    // PUT (404 NOT FOUND)
    @Test
    public void testModifyInmuebleNotFound() throws Exception {
        Long id = 99L;
        InmuebleInDto inDto = new InmuebleInDto("X", 1f, 1, 1d, 1d, true, LocalDate.now(), 1L, 1L);

        when(inmuebleService.modify(eq(id), any(InmuebleInDto.class)))
                .thenThrow(new InmuebleNotFoundException("No existe"));

        mockMvc.perform(MockMvcRequestBuilders.put("/inmuebles/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isNotFound());
    }

    // PUT (400 BAD REQUEST)
    @Test
    public void testModifyInmuebleBadRequest() throws Exception {
        Long id = 1L;
        // Título vacío y precio negativo para forzar el error de validación
        InmuebleInDto invalidDto = new InmuebleInDto("", -100f, null, null, null, null, null, null, null);

        mockMvc.perform(MockMvcRequestBuilders.put("/inmuebles/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    // DELETE (204 NO CONTENT)
    @Test
    public void testDeleteInmueble() throws Exception {
        Long id = 1L;
        doNothing().when(inmuebleService).delete(id);

        mockMvc.perform(MockMvcRequestBuilders.delete("/inmuebles/" + id))
                .andExpect(status().isNoContent());
    }

    // DELETE (404 NOT FOUND)
    @Test
    public void testDeleteInmuebleNotFound() throws Exception {
        Long id = 99L;
        doThrow(new InmuebleNotFoundException("No existe")).when(inmuebleService).delete(id);

        mockMvc.perform(MockMvcRequestBuilders.delete("/inmuebles/" + id))
                .andExpect(status().isNotFound());
    }

    //JPQL ENDPOINT (200 OK)
    @Test
    public void testGetByRango() throws Exception {
        Float min = 100000f;
        Float max = 200000f;
        List<InmuebleOutDto> mockList = List.of(
                new InmuebleOutDto(1L, "Piso Rango", 150000f, 80, 0d, 0d, true, LocalDate.now(), 1L, 1L)
        );

        when(inmuebleService.findInmueblesRangoPrecio(min, max)).thenReturn(mockList);

        mockMvc.perform(MockMvcRequestBuilders.get("/inmuebles/rango")
                        .param("min", String.valueOf(min))
                        .param("max", String.valueOf(max))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Piso Rango"));
    }
}