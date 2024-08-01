package br.com.fiap.hackaton_cartao_app.service;

import br.com.fiap.hackaton_cartao_app.exception.CartaoException;
import br.com.fiap.hackaton_cartao_app.exception.CartaoLimitException;
import br.com.fiap.hackaton_cartao_app.model.Cartao;
import br.com.fiap.hackaton_cartao_app.repository.CartaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CartaoServiceTest {

    @InjectMocks
    private CartaoService cartaoService;

    @Mock
    private CartaoRepository cartaoRepository;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testSalvarCartao_Success() {
        Cartao cartao = new Cartao();
        cartao.setCpf("12345678900");
        cartao.setNumero("1234567812341234");

        when(cartaoRepository.countByCpf(cartao.getCpf())).thenReturn(1L);
        when(cartaoRepository.countByNumero(cartao.getNumero())).thenReturn(0L);
        when(cartaoRepository.save(any(Cartao.class))).thenReturn(cartao);

        Cartao result = cartaoService.salvarCartao(cartao);

        assertNotNull(result);
        assertEquals(cartao, result);
        verify(cartaoRepository).save(cartao);
    }

    @Test
    public void testSalvarCartao_InvalidCPF() {
        Cartao cartao = new Cartao();
        cartao.setCpf("invalidCpf");

        when(cartaoRepository.countByCpf(any(String.class))).thenThrow(new CartaoException("CPF inválido"));

        assertThrows(CartaoException.class, () -> cartaoService.salvarCartao(cartao));
    }

    @Test
    public void testSalvarCartao_CardLimitExceeded() {
        Cartao cartao = new Cartao();
        cartao.setCpf("12345678900");
        cartao.setNumero("1234567890001234");

        when(cartaoRepository.countByCpf(cartao.getCpf())).thenReturn(2L);
        when(cartaoRepository.countByNumero(cartao.getNumero())).thenReturn(0L);

        assertThrows(CartaoLimitException.class, () -> cartaoService.salvarCartao(cartao));
    }

    @Test
    public void testSalvarCartao_DuplicateCardNumber() {
        Cartao cartao = new Cartao();
        cartao.setCpf("12345678900");
        cartao.setNumero("1234567812341234");

        when(cartaoRepository.countByCpf(cartao.getCpf())).thenReturn(1L);
        when(cartaoRepository.countByNumero(cartao.getNumero())).thenReturn(1L);

        assertThrows(CartaoException.class, () -> cartaoService.salvarCartao(cartao));
    }

    @Test
    public void testSalvarCartao_InvalidCardNumberFormat() {
        Cartao cartao = new Cartao();
        cartao.setCpf("12345678900");
        cartao.setNumero("1234567812345678");

        when(cartaoRepository.countByCpf(cartao.getCpf())).thenReturn(1L);
        when(cartaoRepository.countByNumero(cartao.getNumero())).thenReturn(0L);
        when(cartaoService.verificarUltimosDigitos(cartao.getNumero())).thenReturn(false);

        assertThrows(CartaoException.class, () -> cartaoService.salvarCartao(cartao));
    }

    @Test
    public void testConsultarCPF_Valid() {
        String cpf = "12345678900";
        String url = String.format("http://localhost:8083/api/cliente/validarCPF/%s",cpf);
        ResponseEntity<String> responseEntity = new ResponseEntity<>("CPF válido", HttpStatus.OK);

        when(restTemplate.getForEntity(url, String.class)).thenReturn(responseEntity);

        boolean result = cartaoService.consultarCPF(cpf);

        assertTrue(result); // Expect true because "CPF válido" is returned
    }

    @Test
    public void testConsultarCPF_Invalid() {
        String cpf = "12345678900";
        String url = String.format("http://localhost:8083/api/cliente/validarCPF/%s",cpf);
        ResponseEntity<String> responseEntity = new ResponseEntity<>("CPF inválido", HttpStatus.OK);

        when(restTemplate.getForEntity(url, String.class)).thenReturn(responseEntity);

        boolean result = cartaoService.consultarCPF(cpf);

        assertTrue(!result); // Expect false because "CPF inválido" is returned
    }



}