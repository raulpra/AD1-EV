package org.inmobiliaria.apiinmobiliaria.controller;

import jakarta.validation.Valid;
import org.inmobiliaria.apiinmobiliaria.domain.Inmueble;
import org.inmobiliaria.apiinmobiliaria.dto.InmuebleInDto;
import org.inmobiliaria.apiinmobiliaria.dto.InmuebleOutDto;
import org.inmobiliaria.apiinmobiliaria.exception.ErrorResponse;
import org.inmobiliaria.apiinmobiliaria.exception.InmuebleNotFoundException;
import org.inmobiliaria.apiinmobiliaria.service.InmuebleService;
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
public class InmuebleController {

    @Autowired
    private InmuebleService inmuebleService;

    @Autowired
    private ModelMapper modelMapper;

    // 1. GET ALL (Filtros específicos de Inmueble)
    @GetMapping("/inmuebles")
    public ResponseEntity<List<InmuebleOutDto>> getAll(
            @RequestParam(required = false) Float precioMax,
            @RequestParam(required = false) Integer metrosMin,
            @RequestParam(required = false) Boolean ascensor
    ) {
        List<InmuebleOutDto> inmuebles = inmuebleService.findAll(precioMax, metrosMin, ascensor);
        return ResponseEntity.ok(inmuebles);
    }

    // 2. GET BY ID
    @GetMapping("/inmuebles/{id}")
    public ResponseEntity<InmuebleOutDto> getById(@PathVariable long id) throws InmuebleNotFoundException {
        InmuebleOutDto inmueble = inmuebleService.findById(id);
        return ResponseEntity.ok(inmueble);
    }

    // 3. POST
    @PostMapping("/inmuebles")
    public ResponseEntity<InmuebleOutDto> addInmueble(@Valid @RequestBody InmuebleInDto inmuebleInDto) {
        InmuebleOutDto nuevoInmueble = inmuebleService.add(inmuebleInDto);
        return new ResponseEntity<>(nuevoInmueble, HttpStatus.CREATED);
    }

    // 4. PUT
    @PutMapping("/inmuebles/{id}")
    public ResponseEntity<InmuebleOutDto> modifyInmueble(
            @PathVariable long id,
            @Valid @RequestBody InmuebleInDto inmuebleInDto
    ) throws InmuebleNotFoundException {
        InmuebleOutDto inmuebleModificado = inmuebleService.modify(id, inmuebleInDto);
        return ResponseEntity.ok(inmuebleModificado);
    }

    // 5. DELETE
    @DeleteMapping("/inmuebles/{id}")
    public ResponseEntity<Void> deleteInmueble(@PathVariable long id) throws InmuebleNotFoundException {
        inmuebleService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --- MANEJO DE ERRORES ---

    @ExceptionHandler(Exception.class) // Error 500
    public ResponseEntity<ErrorResponse> handleInternalServerError(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.generalError(500, "Internal Server Error", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InmuebleNotFoundException.class) // Error 404
    public ResponseEntity<ErrorResponse> handleException(InmuebleNotFoundException infe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("El inmueble no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) // Error 400
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

    // Endpoint JPQL: Inmuebles por rango de precio
    @GetMapping("/inmuebles/rango")
    public ResponseEntity<List<InmuebleOutDto>> getByRango(
            @RequestParam Float min,
            @RequestParam Float max
    ) {
        List<InmuebleOutDto> inmuebles = inmuebleService.findInmueblesRangoPrecio(min, max);
        return ResponseEntity.ok(inmuebles);
    }
}