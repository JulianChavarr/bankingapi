package com.pruebatecnica.bankingapi.controller;

import com.pruebatecnica.bankingapi.dto.CuentaCreateRequest;
import com.pruebatecnica.bankingapi.dto.CuentaEstadoRequest;
import com.pruebatecnica.bankingapi.dto.CuentaResponse;
import com.pruebatecnica.bankingapi.service.CuentaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CuentaResponse crear(@Valid @RequestBody CuentaCreateRequest request) {
        return cuentaService.crearCuenta(request);
    }

    @GetMapping("/{id}")
    public CuentaResponse obtener(@PathVariable Long id) {
        return cuentaService.obtenerPorId(id);
    }

    @GetMapping("/cliente/{clienteId}")
    public List<CuentaResponse> listarPorCliente(@PathVariable Long clienteId) {
        return cuentaService.listarPorCliente(clienteId);
    }

    @PatchMapping("/{id}/estado")
    public CuentaResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody CuentaEstadoRequest request) {
        return cuentaService.cambiarEstado(id, request);
    }

    @PatchMapping("/{id}/cancelar")
    public CuentaResponse cancelar(@PathVariable Long id) {
        return cuentaService.cancelar(id);
    }
}