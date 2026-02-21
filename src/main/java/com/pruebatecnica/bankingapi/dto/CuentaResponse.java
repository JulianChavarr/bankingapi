package com.pruebatecnica.bankingapi.dto;

import com.pruebatecnica.bankingapi.entity.enums.EstadoCuenta;
import com.pruebatecnica.bankingapi.entity.enums.TipoCuenta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CuentaResponse(
        Long id,
        Long clienteId,
        TipoCuenta tipoCuenta,
        String numeroCuenta,
        EstadoCuenta estado,
        BigDecimal saldo,
        boolean exentaGMF,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaModificacion
) {}