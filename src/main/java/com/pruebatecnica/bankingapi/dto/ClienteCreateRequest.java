package com.pruebatecnica.bankingapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ClienteCreateRequest(
        @NotBlank @Size(min = 2, max = 20) String tipoIdentificacion,
        @NotBlank @Size(min = 3, max = 50) String numeroIdentificacion,
        @NotBlank @Size(min = 2, max = 100) String nombres,
        @NotBlank @Size(min = 2, max = 100) String apellido,
        @NotBlank @Email String email,
        @NotNull @Past LocalDate fechaNacimiento
) {}