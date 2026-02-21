package com.pruebatecnica.bankingapi.repository;

import com.pruebatecnica.bankingapi.entity.CuentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CuentaRepository extends JpaRepository<CuentaEntity, Long> {

    boolean existsByNumeroCuenta(String numeroCuenta);

    List<CuentaEntity> findByClienteId(Long clienteId);
}