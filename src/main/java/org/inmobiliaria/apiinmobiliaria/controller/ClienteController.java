package org.inmobiliaria.apiinmobiliaria.controller;

import jakarta.validation.Valid;
import org.inmobiliaria.apiinmobiliaria.domain.Cliente;
import org.inmobiliaria.apiinmobiliaria.dto.ClienteInDto;
import org.inmobiliaria.apiinmobiliaria.dto.ClienteInDtoV2;
import org.inmobiliaria.apiinmobiliaria.dto.ClienteOutDto;
import org.inmobiliaria.apiinmobiliaria.dto.ClienteOutDtoV2;
import org.inmobiliaria.apiinmobiliaria.exception.ClienteNotFoundException;
import org.inmobiliaria.apiinmobiliaria.exception.ErrorResponse;
import org.inmobiliaria.apiinmobiliaria.service.ClienteService;
import org.inmobiliaria.apiinmobiliaria.service.ClienteServiceV2;
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
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteServiceV2 clienteServiceV2; // Inyectado para la V2

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


// ==========================================================
// MÉTODOS VERSIÓN 2
// ==========================================================

    /**
      1. GET (V2) - CAMBIO DE SALIDA
      Además de ocultar datos por seguridad, la V2 devuelve un
      campo calculado dinámicamente ('diasRegistrado') basado en la 'fechaAlta'.
     */
    @GetMapping("/v2/clientes/{id}")
    public ResponseEntity<ClienteOutDtoV2> getByIdV2(@PathVariable long id) throws ClienteNotFoundException {
        ClienteOutDtoV2 cliente = clienteServiceV2.findById(id);
        return ResponseEntity.ok(cliente);
    }

    /**
      2. POST (V2) - CAMBIO DE ENTRADA
      Se exige un nuevo DTO (ClienteInDtoV2) que incluye el campo
      'tipoVivienda' como obligatorio.
     */
    @PostMapping("/v2/clientes")
    public ResponseEntity<ClienteOutDtoV2> addClienteV2(@Valid @RequestBody ClienteInDtoV2 clienteInDtoV2) {
        ClienteOutDtoV2 nuevoCliente = clienteServiceV2.add(clienteInDtoV2);
        return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED);
    }

    /**
      3. PUT (V2) - CAMBIO DE FUNCIONAMIENTO (Protección de datos)
      Modificamos el funcionamiento del update. Por seguridad, en la V2
      se ignoran los intentos de modificar el 'email' y la 'fechaAlta' del cliente.
     */
    @PutMapping("/v2/clientes/{id}")
    public ResponseEntity<ClienteOutDtoV2> modifyClienteV2(
            @PathVariable long id,
            @Valid @RequestBody ClienteInDtoV2 clienteInDtoV2
    ) throws ClienteNotFoundException {
        ClienteOutDtoV2 clienteModificado = clienteServiceV2.modify(id, clienteInDtoV2);
        return ResponseEntity.ok(clienteModificado);
    }

    /**
      4. DELETE (V2) - CAMBIO DE SALIDA
      La V1 devolvía 204 No Content. La V2 devuelve 200 OK con un
      JSON que incluye datos de auditoría (ID eliminado y fecha/hora de baja).
     */
    @DeleteMapping("/v2/clientes/{id}")
    public ResponseEntity<Map<String, Object>> deleteClienteV2(@PathVariable long id) throws ClienteNotFoundException {
        clienteServiceV2.delete(id);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Cliente dado de baja permanentemente (V2)");
        response.put("id_cliente", id);
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}

