package com.pruebatecnica.bankingapi.repository;

import com.pruebatecnica.bankingapi.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {

    boolean existsByNumeroIdentificacion(String numeroIdentificacion);

    boolean existsByEmail(String email);

    Optional<ClienteEntity> findByNumeroIdentificacion(String numeroIdentificacion);
}