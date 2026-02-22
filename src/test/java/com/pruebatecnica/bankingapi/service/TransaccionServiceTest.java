package com.pruebatecnica.bankingapi.service;

import com.pruebatecnica.bankingapi.dto.RetiroRequest;
import com.pruebatecnica.bankingapi.dto.TransferenciaRequest;
import com.pruebatecnica.bankingapi.entity.CuentaEntity;
import com.pruebatecnica.bankingapi.entity.enums.EstadoCuenta;
import com.pruebatecnica.bankingapi.entity.enums.TipoCuenta;
import com.pruebatecnica.bankingapi.exception.BadRequestException;
import com.pruebatecnica.bankingapi.repository.CuentaRepository;
import com.pruebatecnica.bankingapi.repository.TransaccionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class TransaccionServiceTest {

    @Mock CuentaRepository cuentaRepository;
    @Mock TransaccionRepository transaccionRepository;

    @InjectMocks TransaccionService transaccionService;

    @Test
    void retiro_enAhorros_debeFallar_siDejaNegativo() {
        CuentaEntity ahorros = CuentaEntity.builder()
                .id(1L)
                .tipoCuenta(TipoCuenta.AHORROS)
                .estado(EstadoCuenta.ACTIVA)
                .saldo(new BigDecimal("50.00"))
                .build();

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(ahorros));

        assertThrows(BadRequestException.class,
                () -> transaccionService.retirar(new RetiroRequest(1L, new BigDecimal("100.00"))));

        verify(transaccionRepository, never()).save(any());
    }

    @Test
    void transferencia_debeDebitarOrigen_yAcreditarDestino() {
        CuentaEntity origen = CuentaEntity.builder()
                .id(1L)
                .tipoCuenta(TipoCuenta.AHORROS)
                .estado(EstadoCuenta.ACTIVA)
                .saldo(new BigDecimal("100.00"))
                .build();

        CuentaEntity destino = CuentaEntity.builder()
                .id(2L)
                .tipoCuenta(TipoCuenta.CORRIENTE)
                .estado(EstadoCuenta.ACTIVA)
                .saldo(new BigDecimal("10.00"))
                .build();

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(origen));
        when(cuentaRepository.findById(2L)).thenReturn(Optional.of(destino));

        when(cuentaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(transaccionRepository.save(any())).thenAnswer(inv -> {
            var tx = inv.getArgument(0);
            // no necesitamos setear id para el test
            return tx;
        });

        transaccionService.transferir(new TransferenciaRequest(1L, 2L, new BigDecimal("25.00")));

        assertEquals(new BigDecimal("75.00"), origen.getSaldo());
        assertEquals(new BigDecimal("35.00"), destino.getSaldo());

        verify(transaccionRepository, times(1)).save(any());
    }
}