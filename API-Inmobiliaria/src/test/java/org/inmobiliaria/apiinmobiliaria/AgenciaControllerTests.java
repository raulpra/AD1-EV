package org.inmobiliaria.apiinmobiliaria;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.inmobiliaria.apiinmobiliaria.controller.AgenciaController;
import org.inmobiliaria.apiinmobiliaria.domain.Agencia;
import org.inmobiliaria.apiinmobiliaria.dto.AgenciaInDto;
import org.inmobiliaria.apiinmobiliaria.dto.AgenciaOutDto;
import org.inmobiliaria.apiinmobiliaria.exception.AgenciaNotFoundException;
import org.inmobiliaria.apiinmobiliaria.service.AgenciaService;
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

@WebMvcTest(AgenciaController.class)
public class AgenciaControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AgenciaService agenciaService;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    // GET ALL (200 OK) - Sin filtros
    @Test
    public void testGetAll() throws Exception {
        // Simulamos datos
        List<AgenciaOutDto> agenciasMock = List.of(
                new AgenciaOutDto(1L, "Agencia A", "Calle A", 100f, 28000, true, LocalDate.of(2020, 1, 1)),
                new AgenciaOutDto(2L, "Agencia B", "Calle B", 200f, 28001, false, LocalDate.of(2021, 2, 2))
        );

        // Hacemos mock del service (paso nulls porque no hay filtros)
        when(agenciaService.findAll(null, null, null)).thenReturn(agenciasMock);

        // Ejecución
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/agencias")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Verificación del contenido JSON
        String jsonResponse = result.getResponse().getContentAsString();
        List<AgenciaOutDto> responseList = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(2, responseList.size());
        assertEquals("Agencia A", responseList.get(0).getNombre());
    }


    // GET ALL (200 OK) - Con filtros
    @Test
    public void testGetAllWithFilters() throws Exception {
        //Datos del filtro
        String nombre = "Norte";
        Integer cp = 28001;
        Boolean sabados = true;

        //Simulamos datos
        List<AgenciaOutDto> filteredList = List.of(
                new AgenciaOutDto(1L, "Inmobiliaria Norte", "Calle A", 1000f, 28001, true, LocalDate.now())
        );

        //Hacemos mock del service, pasamos los datos del filtro
        when(agenciaService.findAll(nombre, cp, sabados)).thenReturn(filteredList);

        // Ejecución enviando query params en la URL
        mockMvc.perform(MockMvcRequestBuilders.get("/agencias")
                        .param("nombre", nombre)
                        .param("codigoPostal", String.valueOf(cp))
                        .param("abiertoSabados", String.valueOf(sabados))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Inmobiliaria Norte"));
    }

    // GET BY ID (200 OK)
    @Test
    public void testGetById() throws Exception {
        AgenciaOutDto agenciaMock = new AgenciaOutDto(1L, "Agencia A", "Calle A", 100f, 28000, true, LocalDate.of(2020, 1, 1));

        when(agenciaService.findById(1L)).thenReturn(agenciaMock);

        mockMvc.perform(MockMvcRequestBuilders.get("/agencias/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Agencia A"))
                .andExpect(jsonPath("$.id").value(1));
    }

    //GET BY ID (404 NOT FOUND)
    @Test
    public void testGetByIdNotFound() throws Exception {
        when(agenciaService.findById(99L)).thenThrow(new AgenciaNotFoundException("Agencia no encontrada"));

        mockMvc.perform(MockMvcRequestBuilders.get("/agencias/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    //POST (201 OK)
    @Test
    public void testAddAgencia() throws Exception {
        // Datos entrada DTO
        AgenciaInDto inDto = new AgenciaInDto("Nueva", "Dir", 1000f, 28000, true, LocalDate.of(2023, 1, 1));

        // Simulamos datos
        Agencia agenciaEntity = new Agencia(); agenciaEntity.setNombre("Nueva");
        Agencia agenciaGuardada = new Agencia(); agenciaGuardada.setId(1L); agenciaGuardada.setNombre("Nueva");
        AgenciaOutDto outDto = new AgenciaOutDto(1L, "Nueva", "Dir", 1000f, 28000, true, LocalDate.of(2023, 1, 1));

        // Hacemos mock del service
        when(modelMapper.map(any(AgenciaInDto.class), eq(Agencia.class))).thenReturn(agenciaEntity);
        when(agenciaService.add(any(Agencia.class))).thenReturn(agenciaGuardada);
        when(modelMapper.map(any(Agencia.class), eq(AgenciaOutDto.class))).thenReturn(outDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/agencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Nueva"))
                .andExpect(jsonPath("$.id").value(1));
    }

    //POST (400 BAD REQUEST)
    @Test
    public void testAddAgenciaBadRequest() throws Exception {
        // DTO inválido (nombre vacío)
        AgenciaInDto invalidDto = new AgenciaInDto();
        invalidDto.setNombre("");

        mockMvc.perform(MockMvcRequestBuilders.post("/agencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    // PUT (200 OK)
    @Test
    public void testModifyAgencia() throws Exception {
        Long id = 1L;
        AgenciaInDto inDto = new AgenciaInDto("Modificada", "Dir", 500f, 28000, false, LocalDate.of(2023, 1, 1));
        AgenciaOutDto outDto = new AgenciaOutDto(id, "Modificada", "Dir", 500f, 28000, false, LocalDate.of(2023, 1, 1));

        when(agenciaService.modify(eq(id), any(AgenciaInDto.class))).thenReturn(outDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/agencias/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Modificada"));
    }

    //PUT (404 NOT FOUND)
    @Test
    public void testModifyAgenciaNotFound() throws Exception {
        Long id = 99L;
        AgenciaInDto inDto = new AgenciaInDto("X", "X", 1f, 1, true, LocalDate.of(2023, 1, 1));

        when(agenciaService.modify(eq(id), any(AgenciaInDto.class)))
                .thenThrow(new AgenciaNotFoundException("No existe"));

        mockMvc.perform(MockMvcRequestBuilders.put("/agencias/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inDto)))
                .andExpect(status().isNotFound());
    }

    //PUT (400 BAD REQUEST)
    @Test
    public void testModifyAgenciaBadRequest() throws Exception {
        Long id = 1L;
        AgenciaInDto invalidDto = new AgenciaInDto("", "", null, null, null, null); // Inválido

        mockMvc.perform(MockMvcRequestBuilders.put("/agencias/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    // DELETE (204 NO CONTENT)
    @Test
    public void testDeleteAgencia() throws Exception {
        Long id = 1L;
        doNothing().when(agenciaService).delete(id);

        mockMvc.perform(MockMvcRequestBuilders.delete("/agencias/" + id))
                .andExpect(status().isNoContent());
    }

    // DELETE (404 NOT FOUND)
    @Test
    public void testDeleteAgenciaNotFound() throws Exception {
        Long id = 99L;
        doThrow(new AgenciaNotFoundException("No existe")).when(agenciaService).delete(id);

        mockMvc.perform(MockMvcRequestBuilders.delete("/agencias/" + id))
                .andExpect(status().isNotFound());
    }
}