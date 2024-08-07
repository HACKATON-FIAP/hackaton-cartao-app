package br.com.fiap.hackaton_cartao_app.controller;

import br.com.fiap.hackaton_cartao_app.exception.CartaoException;
import br.com.fiap.hackaton_cartao_app.exception.CartaoLimitException;
import br.com.fiap.hackaton_cartao_app.model.Cartao;
import br.com.fiap.hackaton_cartao_app.service.CartaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class CartaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private CartaoController cartaoController;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @MockBean
    private CartaoService cartaoService;

    private ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void shouldGerarCartao() throws Exception {
        Cartao cartao = new Cartao();
        cartao.setId(1L);
        cartao.setCpf("11111111111");
        cartao.setNumero("5200 1211 1435 1234");
        cartao.setData_validade("12/24");
        cartao.setCvv("123");

        String cartaoJson = objectMapper.writeValueAsString(cartao);

        when(cartaoService.salvarCartao(any(Cartao.class))).thenReturn(cartao);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/cartao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cartaoJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(cartaoJson));

    }

    @Test
    public void testGerarCartao_CartaoLimitException() {
        Cartao cartao = new Cartao();

        when(cartaoService.salvarCartao(any(Cartao.class))).thenThrow(new CartaoLimitException("Limit exceeded"));

        ResponseEntity<Cartao> response = cartaoController.gerarCartao(cartao);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testGerarCartao_CartaoException() {
        Cartao cartao = new Cartao();

        when(cartaoService.salvarCartao(any(Cartao.class))).thenThrow(new CartaoException("Invalid card"));

        ResponseEntity<Cartao> response = cartaoController.gerarCartao(cartao);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testGerarCartao_RuntimeException() {
        Cartao cartao = new Cartao();

        when(cartaoService.salvarCartao(any(Cartao.class))).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<Cartao> response = cartaoController.gerarCartao(cartao);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testConsultarCartaoComSucesso() {
        Cartao cartao = new Cartao();
        cartao.setId(1L);
        cartao.setCpf("11111111111");
        cartao.setNumero("5200 1211 1435 1234");
        cartao.setData_validade("12/24");
        cartao.setCvv("123");

        when(cartaoService.consultarCartao(cartao.getCpf())).thenReturn(cartao);

        ResponseEntity<Cartao> response = cartaoController.consultarCartao(cartao.getCpf());

        assertEquals(HttpStatus.OK, response.getStatusCode());

    }


}