package com.pruebatecnica.bankingapi.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClienteResponse(
        Long id,
        String tipoIdentificacion,
        String numeroIdentificacion,
        String nombres,
        String apellido,
        String email,
        LocalDate fechaNacimiento,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaModificacion
) {}