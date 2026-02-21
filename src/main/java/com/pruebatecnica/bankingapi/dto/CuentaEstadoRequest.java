package com.pruebatecnica.bankingapi.dto;

import com.pruebatecnica.bankingapi.entity.enums.EstadoCuenta;
import jakarta.validation.constraints.NotNull;

public record CuentaEstadoRequest(
        @NotNull EstadoCuenta estado
) {}