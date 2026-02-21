package com.pruebatecnica.bankingapi.controller;

import com.pruebatecnica.bankingapi.dto.ClienteCreateRequest;
import com.pruebatecnica.bankingapi.dto.ClienteUpdateRequest;
import com.pruebatecnica.bankingapi.dto.ClienteResponse;
import com.pruebatecnica.bankingapi.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse crear(@Valid @RequestBody ClienteCreateRequest request) {
        return clienteService.crearCliente(request);
    }

    @GetMapping("/{id}")
    public ClienteResponse obtenerPorId(@PathVariable Long id) {
        return clienteService.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public ClienteResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteUpdateRequest request
    ) {
        return clienteService.actualizarCliente(id, request);
    }
}