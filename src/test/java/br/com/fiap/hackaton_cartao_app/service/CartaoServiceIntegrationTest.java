package br.com.fiap.hackaton_cartao_app.service;


import br.com.fiap.hackaton_cartao_app.exception.CartaoLimitException;
import br.com.fiap.hackaton_cartao_app.model.Cartao;
import br.com.fiap.hackaton_cartao_app.repository.CartaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CartaoServiceIntegrationTest {

    @Autowired
    private CartaoService cartaoService;

    @Autowired
    private CartaoRepository cartaoRepository;

    private final static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("db_cartao")
            .withUsername("postgres")
            .withPassword("teste123");

    static {
        postgresContainer.start();
        System.setProperty("spring.datasource.url", postgresContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgresContainer.getUsername());
        System.setProperty("spring.datasource.password", postgresContainer.getPassword());
    }

    @BeforeEach
    public void setUp() {
        cartaoRepository.deleteAll();
    }


    @Test
    public void testSaveCartao_success() {
        Cartao cartao = new Cartao();
        cartao.setId(1L);
        cartao.setCpf("11111111111");
        cartao.setNumero("5200 1211 1435 1234");
        cartao.setData_validade("12/24");
        cartao.setCvv("123");

        Cartao savedCartao = cartaoService.salvarCartao(cartao);

        assertThat(savedCartao).isNotNull();
        assertThat(savedCartao.getId()).isNotNull();
        assertThat(savedCartao.getCpf()).isEqualTo("11111111111");
    }

    @Test
    @Transactional
    public void testConsultarCartao_Success() {
        Cartao cartao = new Cartao();
        cartao.setId(1L);
        cartao.setCpf("11111111111");
        cartao.setNumero("5200 1211 1435 1234");
        cartao.setData_validade("12/24");
        cartao.setCvv("123");

        Cartao savedCartao = cartaoService.salvarCartao(cartao);

        Cartao cartaoConsulta = cartaoService.consultarCartao("11111111111");
        assertEquals("11111111111", cartao.getCpf());
        assertEquals(cartao, cartaoConsulta);
    }

    @Test
    @Transactional
    public void testConsultarCartao_NotFound() {
        Cartao cartao = cartaoService.consultarCartao("00000000000");
        assertNull(cartao);
    }



}