package com.pruebatecnica.bankingapi.controller;

import com.pruebatecnica.bankingapi.dto.*;
import com.pruebatecnica.bankingapi.service.TransaccionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }

    @PostMapping("/consignacion")
    @ResponseStatus(HttpStatus.CREATED)
    public TransaccionResponse consignar(@Valid @RequestBody ConsignacionRequest request) {
        return transaccionService.consignar(request);
    }

    @PostMapping("/retiro")
    @ResponseStatus(HttpStatus.CREATED)
    public TransaccionResponse retirar(@Valid @RequestBody RetiroRequest request) {
        return transaccionService.retirar(request);
    }

    @PostMapping("/transferencia")
    @ResponseStatus(HttpStatus.CREATED)
    public TransaccionResponse transferir(@Valid @RequestBody TransferenciaRequest request) {
        return transaccionService.transferir(request);
    }
}