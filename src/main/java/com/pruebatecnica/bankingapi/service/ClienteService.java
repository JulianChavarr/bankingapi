package com.pruebatecnica.bankingapi.service;

import com.pruebatecnica.bankingapi.dto.ClienteCreateRequest;
import com.pruebatecnica.bankingapi.dto.ClienteUpdateRequest;
import com.pruebatecnica.bankingapi.dto.ClienteResponse;
import com.pruebatecnica.bankingapi.entity.ClienteEntity;
import com.pruebatecnica.bankingapi.exception.BadRequestException;
import com.pruebatecnica.bankingapi.exception.ConflictException;
import com.pruebatecnica.bankingapi.exception.NotFoundException;
import com.pruebatecnica.bankingapi.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<ClienteResponse> listar() {
        return clienteRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ClienteResponse crearCliente(ClienteCreateRequest req) {
        validarMayorDeEdad(req.fechaNacimiento());

        if (clienteRepository.existsByNumeroIdentificacion(req.numeroIdentificacion())) {
            throw new ConflictException("Ya existe un cliente con esa identificación.");
        }
        if (clienteRepository.existsByEmail(req.email())) {
            throw new ConflictException("Ya existe un cliente con ese email.");
        }

        ClienteEntity entity = ClienteEntity.builder()
                .tipoIdentificacion(req.tipoIdentificacion().trim())
                .numeroIdentificacion(req.numeroIdentificacion().trim())
                .nombres(req.nombres().trim())
                .apellido(req.apellido().trim())
                .email(req.email().trim().toLowerCase())
                .fechaNacimiento(req.fechaNacimiento())
                .build();

        ClienteEntity saved = clienteRepository.save(entity);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ClienteResponse obtenerPorId(Long id) {
        ClienteEntity entity = clienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado."));
        return toResponse(entity);
    }

    @Transactional
    public ClienteResponse actualizarCliente(Long id, ClienteUpdateRequest req) {
        validarMayorDeEdad(req.fechaNacimiento());

        ClienteEntity entity = clienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado."));

        String nuevaIdent = req.numeroIdentificacion().trim();
        String nuevoEmail = req.email().trim().toLowerCase();

        if (!nuevaIdent.equals(entity.getNumeroIdentificacion())
                && clienteRepository.existsByNumeroIdentificacion(nuevaIdent)) {
            throw new ConflictException("Ya existe un cliente con esa identificación.");
        }

        if (!nuevoEmail.equals(entity.getEmail())
                && clienteRepository.existsByEmail(nuevoEmail)) {
            throw new ConflictException("Ya existe un cliente con ese email.");
        }

        entity.setTipoIdentificacion(req.tipoIdentificacion().trim());
        entity.setNumeroIdentificacion(nuevaIdent);
        entity.setNombres(req.nombres().trim());
        entity.setApellido(req.apellido().trim());
        entity.setEmail(nuevoEmail);
        entity.setFechaNacimiento(req.fechaNacimiento());

        ClienteEntity saved = clienteRepository.save(entity);
        return toResponse(saved);
    }

    @Transactional
    public void eliminar(Long id) {

        ClienteEntity cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));

        if (cliente.getCuentas() != null && !cliente.getCuentas().isEmpty()) {
            throw new BadRequestException("No se puede eliminar un cliente con cuentas activas");
        }

        clienteRepository.delete(cliente);
    }

    private void validarMayorDeEdad(LocalDate fechaNacimiento) {
        int edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        if (edad < 18) {
            throw new BadRequestException("El cliente debe ser mayor de edad.");
        }
    }

    private ClienteResponse toResponse(ClienteEntity e) {
        return new ClienteResponse(
                e.getId(),
                e.getTipoIdentificacion(),
                e.getNumeroIdentificacion(),
                e.getNombres(),
                e.getApellido(),
                e.getEmail(),
                e.getFechaNacimiento(),
                e.getFechaCreacion(),
                e.getFechaModificacion()
        );
    }
}