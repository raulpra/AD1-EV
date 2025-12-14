package org.inmobiliaria.apiinmobiliaria.controller;

import jakarta.validation.Valid;
import org.inmobiliaria.apiinmobiliaria.domain.Propietario;
import org.inmobiliaria.apiinmobiliaria.dto.PropietarioInDto;
import org.inmobiliaria.apiinmobiliaria.dto.PropietarioOutDto;
import org.inmobiliaria.apiinmobiliaria.exception.ErrorResponse;
import org.inmobiliaria.apiinmobiliaria.exception.PropietarioNotFoundException;
import org.inmobiliaria.apiinmobiliaria.service.PropietarioService;
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
public class PropietarioController {

    @Autowired
    private PropietarioService propietarioService;

    @Autowired
    private ModelMapper modelMapper;

    // 1. GET ALL (con filtros)
    @GetMapping("/propietarios")
    public ResponseEntity<List<PropietarioOutDto>> getAll(
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean esEmpresa
    ) {
        List<PropietarioOutDto> propietarios = propietarioService.findAll(dni, nombre, esEmpresa);
        return ResponseEntity.ok(propietarios);
    }

    // 2. GET BY ID
    @GetMapping("/propietarios/{id}")
    public ResponseEntity<PropietarioOutDto> getById(@PathVariable long id) throws PropietarioNotFoundException {
        PropietarioOutDto propietario = propietarioService.findById(id);
        return ResponseEntity.ok(propietario);
    }

    // 3. POST
    @PostMapping("/propietarios")
    public ResponseEntity<PropietarioOutDto> addPropietario(@Valid @RequestBody PropietarioInDto propietarioInDto) {
        // Conversión DTO -> Entidad
        Propietario propietario = modelMapper.map(propietarioInDto, Propietario.class);

        Propietario nuevoPropietario = propietarioService.add(propietario);

        // Conversión Entidad -> DTO Salida
        PropietarioOutDto propietarioOutDto = modelMapper.map(nuevoPropietario, PropietarioOutDto.class);
        return new ResponseEntity<>(propietarioOutDto, HttpStatus.CREATED);
    }

    // 4. PUT
    @PutMapping("/propietarios/{id}")
    public ResponseEntity<PropietarioOutDto> modifyPropietario(
            @PathVariable long id,
            @Valid @RequestBody PropietarioInDto propietarioInDto
    ) throws PropietarioNotFoundException {
        PropietarioOutDto propietarioModificado = propietarioService.modify(id, propietarioInDto);
        return ResponseEntity.ok(propietarioModificado);
    }

    // 5. DELETE
    @DeleteMapping("/propietarios/{id}")
    public ResponseEntity<Void> deletePropietario(@PathVariable long id) throws PropietarioNotFoundException {
        propietarioService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --- MANEJO DE ERRORES ---

    @ExceptionHandler(Exception.class) // Error 500
    public ResponseEntity<ErrorResponse> handleInternalServerError(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.generalError(500, "Internal Server Error", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PropietarioNotFoundException.class) // Error 404
    public ResponseEntity<ErrorResponse> handleException(PropietarioNotFoundException pnfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("El propietario no existe");
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
}