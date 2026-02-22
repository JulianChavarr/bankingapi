package com.pruebatecnica.bankingapi.entity;

import com.pruebatecnica.bankingapi.entity.enums.TipoTransaccion;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacciones", indexes = {
        @Index(name = "idx_tx_origen", columnList = "cuenta_origen_id"),
        @Index(name = "idx_tx_destino", columnList = "cuenta_destino_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransaccionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoTransaccion tipo;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monto;

    // Consignación: solo destino
    // Retiro: solo origen
    // Transferencia: ambos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_origen_id")
    private CuentaEntity cuentaOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_destino_id")
    private CuentaEntity cuentaDestino;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @PrePersist
    public void prePersist() {
        this.fecha = LocalDateTime.now();
    }
}