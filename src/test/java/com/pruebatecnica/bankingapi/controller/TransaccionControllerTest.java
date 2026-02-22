package com.pruebatecnica.bankingapi.controller;

import com.pruebatecnica.bankingapi.dto.TransferenciaRequest;
import com.pruebatecnica.bankingapi.dto.TransaccionResponse;
import com.pruebatecnica.bankingapi.entity.enums.TipoTransaccion;
import com.pruebatecnica.bankingapi.service.TransaccionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransaccionController.class)
class TransaccionControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    TransaccionService transaccionService;

    @Test
    void postTransferencia_debeResponder201() throws Exception {
        when(transaccionService.transferir(any())).thenReturn(new TransaccionResponse(
                1L, TipoTransaccion.TRANSFERENCIA, new BigDecimal("10.00"),
                1L, 2L, LocalDateTime.now()
        ));

        TransferenciaRequest req = new TransferenciaRequest(1L, 2L, new BigDecimal("10.00"));

        mvc.perform(post("/api/transacciones/transferencia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }
}