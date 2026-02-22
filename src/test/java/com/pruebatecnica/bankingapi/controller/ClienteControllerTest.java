package com.pruebatecnica.bankingapi.controller;

import com.pruebatecnica.bankingapi.dto.ClienteCreateRequest;
import com.pruebatecnica.bankingapi.dto.ClienteResponse;
import com.pruebatecnica.bankingapi.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    ClienteService clienteService;

    @Test
    void postClientes_debeResponder201_yRetornarJson() throws Exception {
        when(clienteService.crearCliente(any())).thenReturn(new ClienteResponse(
                1L, "CC", "123", "Juan", "Perez", "juan@test.com",
                LocalDate.of(2000, 1, 1),
                LocalDateTime.now(), null
        ));

        ClienteCreateRequest req = new ClienteCreateRequest(
                "CC", "123", "Juan", "Perez", "juan@test.com", LocalDate.of(2000, 1, 1)
        );

        mvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }
}