package com.pruebatecnica.bankingapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RetiroRequest(
        @NotNull Long cuentaId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal monto
) {}