package com.pruebatecnica.bankingapi.dto;

import com.pruebatecnica.bankingapi.entity.enums.TipoCuenta;
import jakarta.validation.constraints.NotNull;

public record CuentaCreateRequest(
        @NotNull Long clienteId,
        @NotNull TipoCuenta tipoCuenta,
        boolean exentaGMF
) {}