package com.pruebatecnica.bankingapi.service;

import com.pruebatecnica.bankingapi.dto.*;
import com.pruebatecnica.bankingapi.entity.CuentaEntity;
import com.pruebatecnica.bankingapi.entity.TransaccionEntity;
import com.pruebatecnica.bankingapi.entity.enums.EstadoCuenta;
import com.pruebatecnica.bankingapi.entity.enums.TipoCuenta;
import com.pruebatecnica.bankingapi.entity.enums.TipoTransaccion;
import com.pruebatecnica.bankingapi.exception.BadRequestException;
import com.pruebatecnica.bankingapi.exception.NotFoundException;
import com.pruebatecnica.bankingapi.repository.CuentaRepository;
import com.pruebatecnica.bankingapi.repository.TransaccionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransaccionService {

    private final CuentaRepository cuentaRepository;
    private final TransaccionRepository transaccionRepository;

    public TransaccionService(CuentaRepository cuentaRepository, TransaccionRepository transaccionRepository) {
        this.cuentaRepository = cuentaRepository;
        this.transaccionRepository = transaccionRepository;
    }

    @Transactional
    public TransaccionResponse consignar(ConsignacionRequest req) {
        CuentaEntity destino = cuentaRepository.findById(req.cuentaId())
                .orElseThrow(() -> new NotFoundException("Cuenta destino no encontrada."));

        validarOperable(destino);

        BigDecimal monto = req.monto();
        destino.setSaldo(destino.getSaldo().add(monto));

        TransaccionEntity tx = TransaccionEntity.builder()
                .tipo(TipoTransaccion.CONSIGNACION)
                .monto(monto)
                .cuentaDestino(destino)
                .build();

        cuentaRepository.save(destino);
        return toResponse(transaccionRepository.save(tx));
    }

    @Transactional
    public TransaccionResponse retirar(RetiroRequest req) {
        CuentaEntity origen = cuentaRepository.findById(req.cuentaId())
                .orElseThrow(() -> new NotFoundException("Cuenta origen no encontrada."));

        validarOperable(origen);

        BigDecimal monto = req.monto();
        validarFondos(origen, monto);

        origen.setSaldo(origen.getSaldo().subtract(monto));

        TransaccionEntity tx = TransaccionEntity.builder()
                .tipo(TipoTransaccion.RETIRO)
                .monto(monto)
                .cuentaOrigen(origen)
                .build();

        cuentaRepository.save(origen);
        return toResponse(transaccionRepository.save(tx));
    }

    @Transactional
    public TransaccionResponse transferir(TransferenciaRequest req) {
        if (req.cuentaOrigenId().equals(req.cuentaDestinoId())) {
            throw new BadRequestException("La cuenta origen y destino no pueden ser la misma.");
        }

        CuentaEntity origen = cuentaRepository.findById(req.cuentaOrigenId())
                .orElseThrow(() -> new NotFoundException("Cuenta origen no encontrada."));
        CuentaEntity destino = cuentaRepository.findById(req.cuentaDestinoId())
                .orElseThrow(() -> new NotFoundException("Cuenta destino no encontrada."));

        validarOperable(origen);
        validarOperable(destino);

        BigDecimal monto = req.monto();
        validarFondos(origen, monto);

        origen.setSaldo(origen.getSaldo().subtract(monto));
        destino.setSaldo(destino.getSaldo().add(monto));

        TransaccionEntity tx = TransaccionEntity.builder()
                .tipo(TipoTransaccion.TRANSFERENCIA)
                .monto(monto)
                .cuentaOrigen(origen)
                .cuentaDestino(destino)
                .build();

        cuentaRepository.save(origen);
        cuentaRepository.save(destino);
        return toResponse(transaccionRepository.save(tx));
    }

    // ---- Reglas ----

    private void validarOperable(CuentaEntity cuenta) {
        if (cuenta.getEstado() == EstadoCuenta.CANCELADA) {
            throw new BadRequestException("No se puede operar una cuenta CANCELADA.");
        }
        // En la prueba normalmente también bloquean INACTIVA:
        if (cuenta.getEstado() == EstadoCuenta.INACTIVA) {
            throw new BadRequestException("No se puede operar una cuenta INACTIVA.");
        }
    }

    private void validarFondos(CuentaEntity origen, BigDecimal monto) {
        // Regla del enunciado: AHORROS no puede quedar con saldo < 0
        if (origen.getTipoCuenta() == TipoCuenta.AHORROS) {
            BigDecimal saldoResultante = origen.getSaldo().subtract(monto);
            if (saldoResultante.compareTo(BigDecimal.ZERO) < 0) {
                throw new BadRequestException("Saldo insuficiente: una cuenta de AHORROS no puede quedar en negativo.");
            }
        }
        // CORRIENTE: si el enunciado no permite negativo, aquí también se valida.
        // Por ahora dejamos CORRIENTE con libertad (según interpretación típica).
    }

    private TransaccionResponse toResponse(TransaccionEntity tx) {
        Long origenId = (tx.getCuentaOrigen() != null) ? tx.getCuentaOrigen().getId() : null;
        Long destinoId = (tx.getCuentaDestino() != null) ? tx.getCuentaDestino().getId() : null;

        return new TransaccionResponse(
                tx.getId(),
                tx.getTipo(),
                tx.getMonto(),
                origenId,
                destinoId,
                tx.getFecha()
        );
    }
}