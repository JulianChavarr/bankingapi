package com.pruebatecnica.bankingapi.service;

import com.pruebatecnica.bankingapi.dto.CuentaCreateRequest;
import com.pruebatecnica.bankingapi.dto.CuentaEstadoRequest;
import com.pruebatecnica.bankingapi.dto.CuentaResponse;
import com.pruebatecnica.bankingapi.entity.ClienteEntity;
import com.pruebatecnica.bankingapi.entity.CuentaEntity;
import com.pruebatecnica.bankingapi.entity.enums.EstadoCuenta;
import com.pruebatecnica.bankingapi.entity.enums.TipoCuenta;
import com.pruebatecnica.bankingapi.exception.BadRequestException;
import com.pruebatecnica.bankingapi.exception.NotFoundException;
import com.pruebatecnica.bankingapi.repository.ClienteRepository;
import com.pruebatecnica.bankingapi.repository.CuentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

@Service
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;

    private final SecureRandom random = new SecureRandom();

    public CuentaService(CuentaRepository cuentaRepository, ClienteRepository clienteRepository) {
        this.cuentaRepository = cuentaRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public CuentaResponse crearCuenta(CuentaCreateRequest req) {
        ClienteEntity cliente = clienteRepository.findById(req.clienteId())
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado."));

        String numeroCuenta = generarNumeroCuentaUnico(req.tipoCuenta());

        // Regla: al crear, saldo por defecto 0.
        // Regla: AHORROS debe quedar ACTIVA por defecto (y en general es lo más lógico para CORRIENTE también).
        EstadoCuenta estadoInicial = EstadoCuenta.ACTIVA;

        CuentaEntity cuenta = CuentaEntity.builder()
                .cliente(cliente)
                .tipoCuenta(req.tipoCuenta())
                .numeroCuenta(numeroCuenta)
                .estado(estadoInicial)
                .saldo(BigDecimal.ZERO)
                .exentaGMF(req.exentaGMF())
                .build();

        CuentaEntity saved = cuentaRepository.save(cuenta);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public CuentaResponse obtenerPorId(Long cuentaId) {
        CuentaEntity cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new NotFoundException("Cuenta no encontrada."));
        return toResponse(cuenta);
    }

    @Transactional(readOnly = true)
    public List<CuentaResponse> listarPorCliente(Long clienteId) {
        // Validación opcional: que exista el cliente.
        if (!clienteRepository.existsById(clienteId)) {
            throw new NotFoundException("Cliente no encontrado.");
        }
        return cuentaRepository.findByClienteId(clienteId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public CuentaResponse cambiarEstado(Long cuentaId, CuentaEstadoRequest req) {
        CuentaEntity cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new NotFoundException("Cuenta no encontrada."));

        if (req.estado() == EstadoCuenta.CANCELADA) {
            throw new BadRequestException("Para cancelar use el endpoint de cancelación.");
        }

        // Regla: activar/inactivar en cualquier momento
        cuenta.setEstado(req.estado());
        return toResponse(cuentaRepository.save(cuenta));
    }

    @Transactional
    public CuentaResponse cancelar(Long cuentaId) {
        CuentaEntity cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new NotFoundException("Cuenta no encontrada."));

        // Regla: solo cancelar si saldo = 0
        if (cuenta.getSaldo() == null || cuenta.getSaldo().compareTo(BigDecimal.ZERO) != 0) {
            throw new BadRequestException("Solo se puede cancelar si el saldo es 0.");
        }

        cuenta.setEstado(EstadoCuenta.CANCELADA);
        return toResponse(cuentaRepository.save(cuenta));
    }

    // ---------- Helpers ----------

    private String generarNumeroCuentaUnico(TipoCuenta tipoCuenta) {
        String prefijo = (tipoCuenta == TipoCuenta.AHORROS) ? "53" : "33";

        // 10 dígitos total: 2 de prefijo + 8 random
        // Intentos para evitar colisión (muy improbable, pero seguro)
        for (int i = 0; i < 20; i++) {
            String candidato = prefijo + random8Digits();
            if (!cuentaRepository.existsByNumeroCuenta(candidato)) {
                return candidato;
            }
        }
        throw new BadRequestException("No fue posible generar un número de cuenta único. Intente de nuevo.");
    }

    private String random8Digits() {
        int n = random.nextInt(100_000_000); // 0..99,999,999
        return String.format("%08d", n);
    }

    private CuentaResponse toResponse(CuentaEntity c) {
        return new CuentaResponse(
                c.getId(),
                c.getCliente().getId(),
                c.getTipoCuenta(),
                c.getNumeroCuenta(),
                c.getEstado(),
                c.getSaldo(),
                c.isExentaGMF(),
                c.getFechaCreacion(),
                c.getFechaModificacion()
        );
    }
}