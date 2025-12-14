package org.inmobiliaria.apiinmobiliaria.controller;

import jakarta.validation.Valid;
import org.inmobiliaria.apiinmobiliaria.domain.Visita;
import org.inmobiliaria.apiinmobiliaria.dto.VisitaInDto;
import org.inmobiliaria.apiinmobiliaria.dto.VisitaOutDto;
import org.inmobiliaria.apiinmobiliaria.exception.ErrorResponse;
import org.inmobiliaria.apiinmobiliaria.exception.VisitaNotFoundException;
import org.inmobiliaria.apiinmobiliaria.service.VisitaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class VisitaController {

    @Autowired
    private VisitaService visitaService;

    @Autowired
    private ModelMapper modelMapper;

    // 1. GET ALL
    @GetMapping("/visitas")
    public ResponseEntity<List<VisitaOutDto>> getAll(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) LocalDateTime fechaDesde,
            @RequestParam(required = false) Float valoracionMin
    ) {
        List<VisitaOutDto> visitas = visitaService.findAll(estado, fechaDesde, valoracionMin);
        return ResponseEntity.ok(visitas);
    }

    // 2. GET BY ID
    @GetMapping("/visitas/{id}")
    public ResponseEntity<VisitaOutDto> getById(@PathVariable long id) throws VisitaNotFoundException {
        VisitaOutDto visita = visitaService.findById(id);
        return ResponseEntity.ok(visita);
    }

    // 3. POST
    @PostMapping("/visitas")
    public ResponseEntity<VisitaOutDto> addVisita(@Valid @RequestBody VisitaInDto visitaInDto) {
        // Ya no hacemos mapeo aquí. Pasamos el DTO directamente al servicio.
        // El servicio buscará el Cliente y el Inmueble y devolverá el resultado completo.
        VisitaOutDto nuevaVisita = visitaService.add(visitaInDto);

        return new ResponseEntity<>(nuevaVisita, HttpStatus.CREATED);
    }

    // 4. PUT
    @PutMapping("/visitas/{id}")
    public ResponseEntity<VisitaOutDto> modifyVisita(
            @PathVariable long id,
            @Valid @RequestBody VisitaInDto visitaInDto
    ) throws VisitaNotFoundException {
        VisitaOutDto visitaModificada = visitaService.modify(id, visitaInDto);
        return ResponseEntity.ok(visitaModificada);
    }

    // 5. DELETE
    @DeleteMapping("/visitas/{id}")
    public ResponseEntity<Void> deleteVisita(@PathVariable long id) throws VisitaNotFoundException {
        visitaService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --- ERRORES ---

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.generalError(500, "Internal Server Error", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(VisitaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(VisitaNotFoundException vnfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("La visita no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException manve) {
        Map<String, String> errors = new HashMap<>();
        manve.getBindingResult().getAllErrors().forEach(error -> {
            errors.put(((FieldError) error).getField(), error.getDefaultMessage());
        });
        return new ResponseEntity<>(ErrorResponse.validationError(errors), HttpStatus.BAD_REQUEST);
    }
}