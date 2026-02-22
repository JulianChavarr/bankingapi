package com.pruebatecnica.bankingapi.service;

import com.pruebatecnica.bankingapi.dto.ClienteCreateRequest;
import com.pruebatecnica.bankingapi.exception.BadRequestException;
import com.pruebatecnica.bankingapi.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    ClienteRepository clienteRepository;

    @InjectMocks
    ClienteService clienteService;

    @Test
    void crearCliente_debeFallar_siEsMenorDeEdad() {
        ClienteCreateRequest req = new ClienteCreateRequest(
                "CC",
                "123",
                "Juan",
                "Perez",
                "juan@test.com",
                LocalDate.now().minusYears(10)
        );

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> clienteService.crearCliente(req));

        assertTrue(ex.getMessage().toLowerCase().contains("mayor"));
        verifyNoInteractions(clienteRepository);
    }

    @Test
    void crearCliente_debeFallar_siIdentificacionRepetida() {
        ClienteCreateRequest req = new ClienteCreateRequest(
                "CC",
                "123",
                "Juan",
                "Perez",
                "juan@test.com",
                LocalDate.now().minusYears(20)
        );

        when(clienteRepository.existsByNumeroIdentificacion("123")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> clienteService.crearCliente(req));
        verify(clienteRepository, times(1)).existsByNumeroIdentificacion("123");
        verify(clienteRepository, never()).save(any());
    }
}