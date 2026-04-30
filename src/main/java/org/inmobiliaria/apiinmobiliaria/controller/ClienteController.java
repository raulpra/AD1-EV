package org.inmobiliaria.apiinmobiliaria.controller;

import jakarta.validation.Valid;
import org.inmobiliaria.apiinmobiliaria.domain.Cliente;
import org.inmobiliaria.apiinmobiliaria.dto.ClienteInDto;
import org.inmobiliaria.apiinmobiliaria.dto.ClienteOutDto;
import org.inmobiliaria.apiinmobiliaria.exception.ClienteNotFoundException;
import org.inmobiliaria.apiinmobiliaria.exception.ErrorResponse;
import org.inmobiliaria.apiinmobiliaria.service.ClienteService;
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
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ModelMapper modelMapper;

    // 1. GET ALL (con filtros
    @GetMapping("/clientes")
    public ResponseEntity<List<ClienteOutDto>> getAll(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) Boolean suscrito
    ) {
        List<ClienteOutDto> clientes = clienteService.findAll(email, telefono, suscrito);
        return ResponseEntity.ok(clientes);
    }

    // 2. GET BY ID
    @GetMapping("/clientes/{id}")
    public ResponseEntity<ClienteOutDto> getById(@PathVariable long id) throws ClienteNotFoundException {
        ClienteOutDto cliente = clienteService.findById(id);
        return ResponseEntity.ok(cliente);
    }

    // 3. POST
    @PostMapping("/clientes")
    public ResponseEntity<ClienteOutDto> addCliente(@Valid @RequestBody ClienteInDto clienteInDto) {
        Cliente cliente = modelMapper.map(clienteInDto, Cliente.class);
        Cliente nuevoCliente = clienteService.add(cliente);
        ClienteOutDto clienteOutDto = modelMapper.map(nuevoCliente, ClienteOutDto.class);
        return new ResponseEntity<>(clienteOutDto, HttpStatus.CREATED);
    }

    // 4. PUT
    @PutMapping("/clientes/{id}")
    public ResponseEntity<ClienteOutDto> modifyCliente(
            @PathVariable long id,
            @Valid @RequestBody ClienteInDto clienteInDto
    ) throws ClienteNotFoundException {
        ClienteOutDto clienteModificado = clienteService.modify(id, clienteInDto);
        return ResponseEntity.ok(clienteModificado);
    }

    // 5. DELETE
    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable long id) throws ClienteNotFoundException {
        clienteService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --- ERRORES ---

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.generalError(500, "Internal Server Error", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ClienteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(ClienteNotFoundException cnfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("El cliente no existe");
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

    // Endpoint JPQL: Clientes VIP
    @GetMapping("/clientes/vip")
    public ResponseEntity<List<ClienteOutDto>> getClientesVip(@RequestParam Float presupuesto) {
        List<ClienteOutDto> clientes = clienteService.findClientesVip(presupuesto);
        return ResponseEntity.ok(clientes);
    }
}