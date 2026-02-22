package com.pruebatecnica.bankingapi.dto;

import com.pruebatecnica.bankingapi.entity.enums.TipoTransaccion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransaccionResponse(
        Long id,
        TipoTransaccion tipo,
        BigDecimal monto,
        Long cuentaOrigenId,
        Long cuentaDestinoId,
        LocalDateTime fecha
) {}