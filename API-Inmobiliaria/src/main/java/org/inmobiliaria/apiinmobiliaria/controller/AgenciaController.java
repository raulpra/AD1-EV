package org.inmobiliaria.apiinmobiliaria.controller;

import jakarta.validation.Valid;
import org.inmobiliaria.apiinmobiliaria.domain.Agencia;
import org.inmobiliaria.apiinmobiliaria.dto.AgenciaInDto;
import org.inmobiliaria.apiinmobiliaria.dto.AgenciaOutDto;
import org.inmobiliaria.apiinmobiliaria.exception.AgenciaNotFoundException;
import org.inmobiliaria.apiinmobiliaria.exception.ErrorResponse;
import org.inmobiliaria.apiinmobiliaria.service.AgenciaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AgenciaController {

    @Autowired
    private AgenciaService agenciaService;

    @Autowired
    private ModelMapper modelMapper;

    // 1. GET ALL (Con filtros opcionales)
    @GetMapping("/agencias")
    public ResponseEntity<List<AgenciaOutDto>> getAll(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer codigoPostal,
            @RequestParam(required = false) Boolean abiertoSabados
    ) {
        List<AgenciaOutDto> agencias = agenciaService.findAll(nombre, codigoPostal, abiertoSabados);
        return ResponseEntity.ok(agencias);
    }

    // 2. GET BY ID
    @GetMapping("/agencias/{id}")
    public ResponseEntity<AgenciaOutDto> getById(@PathVariable long id) throws AgenciaNotFoundException {
        AgenciaOutDto agencia = agenciaService.findById(id);

        //sin abreviar: return new ResponseEntity<>(agencia, HttpStatus.OK);
        return ResponseEntity.ok(agencia);
    }

    // 3. POST (Crear nueva)
    @PostMapping("/agencias")
    public ResponseEntity<AgenciaOutDto> addAgencia(@Valid @RequestBody AgenciaInDto agenciaInDto) {
        // A. Convertimos el DTO de entrada a Entidad (porque el servicio espera Entidad)
        //pero podríamos trabajar solo con la Entidad como en la capa Service
        Agencia agencia = modelMapper.map(agenciaInDto, Agencia.class);

        // B. Llamamos al servicio
        Agencia nuevaAgencia = agenciaService.add(agencia);

        // C. Convertimos la Entidad guardada a DTO de salida
        AgenciaOutDto agenciaOutDto = modelMapper.map(nuevaAgencia, AgenciaOutDto.class);

        return new ResponseEntity<>(agenciaOutDto, HttpStatus.CREATED);
    }

    // 4. PUT (Modificar)
    @PutMapping("/agencias/{id}")
    public ResponseEntity<AgenciaOutDto> modifyAgencia(
            @PathVariable long id,
            @Valid @RequestBody AgenciaInDto agenciaInDto
    ) throws AgenciaNotFoundException {
        // Aquí tu Servicie SÍ acepta el DTO directamente, así que es más limpio
        AgenciaOutDto agenciaModificada = agenciaService.modify(id, agenciaInDto);

        //abreviado: return ResponseEntity.ok(agenciaModificada)
        return new ResponseEntity<>(agenciaModificada, HttpStatus.OK);
    }

    // 5. DELETE
    @DeleteMapping("/agencias/{id}")
    public ResponseEntity<Void> deleteAgencia(@PathVariable long id) throws AgenciaNotFoundException {
        agenciaService.delete(id);

        //abreviado: return ResponseEntity.noContent().build();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content (estándar para borrados)
    }

    @ExceptionHandler(Exception.class) //Error 500
    public ResponseEntity<ErrorResponse> handleInternalServerError(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.generalError(500, "Internal Server Error", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AgenciaNotFoundException.class) //Error 404
    public ResponseEntity<ErrorResponse> handleException(AgenciaNotFoundException anfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("La agencia no existe");

        //abreviado: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) //Error 400
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException manve) {
        Map<String, String> errors = new HashMap<>();
        manve.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        ErrorResponse errorResponse = ErrorResponse.validationError(errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}