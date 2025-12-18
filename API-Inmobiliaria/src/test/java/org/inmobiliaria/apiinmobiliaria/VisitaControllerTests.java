package org.inmobiliaria.apiinmobiliaria;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.inmobiliaria.apiinmobiliaria.controller.VisitaController;
import org.inmobiliaria.apiinmobiliaria.dto.VisitaInDto;
import org.inmobiliaria.apiinmobiliaria.dto.VisitaOutDto;
import org.inmobiliaria.apiinmobiliaria.exception.VisitaNotFoundException;
import org.inmobiliaria.apiinmobiliaria.service.VisitaService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisitaController.class)
public class VisitaControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VisitaService visitaService;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    // GET ALL (200 OK) - Sin filtros
    @Test
    public void testGetAll() throws Exception {
        // Datos simulados
        List<VisitaOutDto> mockList = List.of(
                new VisitaOutDto(1L, LocalDateTime.now(), "Visita 1", "PENDIENTE", 4f, 30, true, 1L, 1L),
                new VisitaOutDto(2L, LocalDateTime.now(), "Visita 2", "CONFIRMADA", 5f, 60, false, 2L, 2L)
        );

        // Mock del servicio
        when(visitaService.findAll(null, null, null)).thenReturn(mockList);

        // Ejecución
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/visitas")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Verificación
        String jsonResponse = result.getResponse().getContentAsString();
        List<VisitaOutDto> responseList = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(2, responseList.size());
        assertEquals("Visita 1", responseList.get(0).getComentarios());
    }

    // GET ALL (200 OK) - Con filtros
    @Test
    public void testGetAllWithFilters() throws Exception {
        // Datos de filtro
        String estado = "PENDIENTE";
        LocalDateTime fechaDesde = LocalDateTime.of(2025, 1, 1, 10, 0);
        Float valoracionMin = 4.0f;

        // Respuesta simulada
        List<VisitaOutDto> filteredList = List.of(
                new VisitaOutDto(1L, fechaDesde, "Visita Filtrada", estado, 5f, 30, true, 1L, 1L)
        );

        // Mock (Usamos any() para la fecha para evitar problemas estrictos de nanosegundos en el test)
        when(visitaService.findAll(eq(estado), any(LocalDateTime.class), eq(valoracionMin))).thenReturn(filteredList);

        // Ejecución enviando query params
        // Spring convierte automáticamente String ISO-8601 a LocalDateTime
        mockMvc.perform(MockMvcRequestBuilders.get("/visitas")
                        .param("estado", estado)
                        .param("fechaDesde", "2025-01-01T10:00:00")
                        .param("valoracionMin", String.valueOf(valoracionMin))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].comentarios").value("Visita Filtrada"));
    }

    // 2. GET BY ID (200 OK)
    @Test
    public void testGetById() throws Exception {
        VisitaOutDto dto = new VisitaOutDto(1L, LocalDateTime.now(), "Test", "PENDIENTE", 5f, 30, true, 1L, 1L);

        when(visitaService.findById(1L)).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/visitas/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comentarios").value("Test"))
                .andExpect(jsonPath("$.id").value(1));
    }

    // GET BY ID (404 NOT FOUND)
    @Test
    public void testGetByIdNotFound() throws Exception {
        when(visitaService.findById(99L)).thenThrow(new VisitaNotFoundException("No existe"));

        mockMvc.perform(MockMvcRequestBuilders.get("/visitas/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // POST (201 CREATED)
    @Test
    public void testAddVisita() throws Exception {
        // Input DTO (Datos válidos)
        VisitaInDto inDto = new VisitaInDto(LocalDateTime.now(), "Nueva", "PENDIENTE", 4f, 30, true, 1L, 2L);

        // Output DTO
        VisitaOutDto outDto = new VisitaOutDto(1L, LocalDateTime.now(), "Nueva", "PENDIENTE", 4f, 30, true, 1L, 2L);

        // Mocking: Controller pasa DTO directo al Service
        when(visitaService.add(any(VisitaInDto.class))).thenReturn(outDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/visitas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comentarios").value("Nueva"))
                .andExpect(jsonPath("$.id").value(1));
    }

    // POST (400 BAD REQUEST)
    @Test
    public void testAddVisitaBadRequest() throws Exception {
        // DTO inválido: Estado incorrecto (Regex fallará) y IDs nulos
        VisitaInDto invalidDto = new VisitaInDto(LocalDateTime.now(), "X", "ESTADO_INVENTADO", 4f, 30, true, null, null);

        mockMvc.perform(MockMvcRequestBuilders.post("/visitas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    // PUT (200 OK)
    @Test
    public void testModifyVisita() throws Exception {
        Long id = 1L;
        VisitaInDto inDto = new VisitaInDto(LocalDateTime.now(), "Editada", "CONFIRMADA", 5f, 45, false, 1L, 2L);
        VisitaOutDto outDto = new VisitaOutDto(id, LocalDateTime.now(), "Editada", "CONFIRMADA", 5f, 45, false, 1L, 2L);

        when(visitaService.modify(eq(id), any(VisitaInDto.class))).thenReturn(outDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/visitas/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comentarios").value("Editada"))
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));
    }

    // PUT - NOT FOUND (404)
    @Test
    public void testModifyVisitaNotFound() throws Exception {
        Long id = 99L;
        // DTO válido pero ID inexistente
        VisitaInDto inDto = new VisitaInDto(LocalDateTime.now(), "X", "PENDIENTE", 1f, 10, true, 1L, 1L);

        when(visitaService.modify(eq(id), any(VisitaInDto.class)))
                .thenThrow(new VisitaNotFoundException("No existe"));

        mockMvc.perform(MockMvcRequestBuilders.put("/visitas/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isNotFound());
    }

    // PUT (400 BAD REQUEST)
    @Test
    public void testModifyVisitaBadRequest() throws Exception {
        Long id = 1L;
        // DTO Inválido (IDs nulos)
        VisitaInDto invalidDto = new VisitaInDto(LocalDateTime.now(), "X", "PENDIENTE", 1f, 10, true, null, null);

        mockMvc.perform(MockMvcRequestBuilders.put("/visitas/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    // DELETE (204 NO CONTENT)
    @Test
    public void testDeleteVisita() throws Exception {
        Long id = 1L;
        doNothing().when(visitaService).delete(id);

        mockMvc.perform(MockMvcRequestBuilders.delete("/visitas/" + id))
                .andExpect(status().isNoContent());
    }

    // DELETE (404 NOT FOUND)
    @Test
    public void testDeleteVisitaNotFound() throws Exception {
        Long id = 99L;
        doThrow(new VisitaNotFoundException("No existe")).when(visitaService).delete(id);

        mockMvc.perform(MockMvcRequestBuilders.delete("/visitas/" + id))
                .andExpect(status().isNotFound());
    }
}