package com.pruebatecnica.bankingapi.service;

import com.pruebatecnica.bankingapi.dto.CuentaCreateRequest;
import com.pruebatecnica.bankingapi.dto.CuentaEstadoRequest;
import com.pruebatecnica.bankingapi.entity.ClienteEntity;
import com.pruebatecnica.bankingapi.entity.CuentaEntity;
import com.pruebatecnica.bankingapi.entity.enums.EstadoCuenta;
import com.pruebatecnica.bankingapi.entity.enums.TipoCuenta;
import com.pruebatecnica.bankingapi.exception.BadRequestException;
import com.pruebatecnica.bankingapi.repository.ClienteRepository;
import com.pruebatecnica.bankingapi.repository.CuentaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CuentaServiceTest {

    @Mock CuentaRepository cuentaRepository;
    @Mock ClienteRepository clienteRepository;

    @InjectMocks CuentaService cuentaService;

    @Test
    void crearCuenta_ahorros_debeGenerarNumeroConPrefijo53_y10Digitos() {
        ClienteEntity cliente = ClienteEntity.builder().id(1L).build();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        // Para evitar colisiones, asumimos que no existe el numero generado:
        when(cuentaRepository.existsByNumeroCuenta(anyString())).thenReturn(false);

        // Capturamos lo que se guarda
        ArgumentCaptor<CuentaEntity> captor = ArgumentCaptor.forClass(CuentaEntity.class);
        when(cuentaRepository.save(captor.capture())).thenAnswer(inv -> {
            CuentaEntity c = captor.getValue();
            c.setId(10L);
            return c;
        });

        var resp = cuentaService.crearCuenta(new CuentaCreateRequest(1L, TipoCuenta.AHORROS, true));

        assertNotNull(resp.numeroCuenta());
        assertEquals(10, resp.numeroCuenta().length());
        assertTrue(resp.numeroCuenta().startsWith("53"));
        assertEquals(EstadoCuenta.ACTIVA, resp.estado());
        assertEquals(0, resp.saldo().compareTo(BigDecimal.ZERO));
    }

    @Test
    void cambiarEstado_noDebePermitirEnviarCancelada_porEndpointEstado() {
        CuentaEntity cuenta = CuentaEntity.builder()
                .id(1L)
                .estado(EstadoCuenta.ACTIVA)
                .saldo(BigDecimal.ZERO)
                .build();

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));

        assertThrows(BadRequestException.class,
                () -> cuentaService.cambiarEstado(1L, new CuentaEstadoRequest(EstadoCuenta.CANCELADA)));
    }

    @Test
    void cancelar_debeFallar_siSaldoNoEsCero() {
        CuentaEntity cuenta = CuentaEntity.builder()
                .id(1L)
                .estado(EstadoCuenta.ACTIVA)
                .saldo(new BigDecimal("100.00"))
                .build();

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));

        assertThrows(BadRequestException.class, () -> cuentaService.cancelar(1L));
        verify(cuentaRepository, never()).save(any());
    }

    @Test
    void cancelar_debeFuncionar_siSaldoEsCero() {
        CuentaEntity cuenta = CuentaEntity.builder()
                .id(1L)
                .estado(EstadoCuenta.ACTIVA)
                .saldo(BigDecimal.ZERO)
                .cliente(ClienteEntity.builder().id(99L).build())
                .tipoCuenta(TipoCuenta.AHORROS)
                .numeroCuenta("5300000001")
                .exentaGMF(false)
                .build();

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var resp = cuentaService.cancelar(1L);

        assertEquals(EstadoCuenta.CANCELADA, resp.estado());
    }
}