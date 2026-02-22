package com.pruebatecnica.bankingapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferenciaRequest(
        @NotNull Long cuentaOrigenId,
        @NotNull Long cuentaDestinoId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal monto
) {}