package br.com.fiap.hackaton_cartao_app.service;

import br.com.fiap.hackaton_cartao_app.exception.CartaoException;
import br.com.fiap.hackaton_cartao_app.exception.CartaoLimitException;
import br.com.fiap.hackaton_cartao_app.model.Cartao;
import br.com.fiap.hackaton_cartao_app.repository.CartaoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
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
    void testSalvarCartaoWithValidCPFAndValidCardNumber() {
        Cartao cartao = new Cartao();
        cartao.setId(1L);
        cartao.setCpf("11111111111");
        cartao.setNumero("5200 1211 1435 1234");
        cartao.setData_validade("12/24");
        cartao.setCvv("123");

        when(cartaoRepository.countByCpf(cartao.getCpf())).thenReturn(1L);
        when(cartaoRepository.countByNumero(cartao.getNumero())).thenReturn(0L);
        when(cartaoRepository.save(cartao)).thenReturn(cartao);

        Cartao savedCartao = cartaoService.salvarCartao(cartao);

        assertNotNull(savedCartao);
        assertEquals("5200 1211 1435 1234", savedCartao.getNumero());
        verify(cartaoRepository).save(cartao);
    }

    @Test
    public void testSalvarCartao_WhenCountExceedsLimit_ShouldThrowCartaoLimitException() {
        // Arrange
        Cartao cartao = new Cartao();
        cartao.setId(1L);
        cartao.setCpf("11111111111");
        cartao.setNumero("5200 1211 1435 1234");
        cartao.setData_validade("12/24");
        cartao.setCvv("123");


        // Mock the repository to return a count that exceeds the limit
        when(cartaoRepository.countByCpf(cartao.getCpf())).thenReturn((long) (2));

        // Act & Assert
        CartaoLimitException exception = assertThrows(CartaoLimitException.class, () -> {
            cartaoService.salvarCartao(cartao);
        });
        assertEquals("Não é permitido ter mais de dois cartões para o mesmo CPF", exception.getMessage());
    }

    @Test
    public void testSalvarCartao_WhenCardNumberAlreadyExists_ShouldThrowCartaoException() {
        Cartao cartao = new Cartao();
        cartao.setId(1L);
        cartao.setCpf("11111111111");
        cartao.setNumero("5200 1211 1435 1234");
        cartao.setData_validade("12/24");
        cartao.setCvv("123");

        // Mock the repository to return a count greater than 0 for the card number
        when(cartaoRepository.countByNumero(cartao.getNumero())).thenReturn((long) (1));

        // Act & Assert
        CartaoException exception = Assertions.assertThrows(CartaoException.class, () -> {
            cartaoService.salvarCartao(cartao);
        });
        Assertions.assertEquals("Não é permitido dois cartões com o mesmo número", exception.getMessage());
    }

    @Test
    public void testSalvarCartao_InvalidUltimosDigitos() {
        // Arrange
        Cartao cartao = new Cartao();
        cartao.setId(1L);
        cartao.setCpf("11111111111");
        cartao.setNumero("5200 1211 1435 4444");
        cartao.setData_validade("12/24");
        cartao.setCvv("123");

        // Act & Assert
        CartaoException thrown = assertThrows(CartaoException.class, () -> cartaoService.salvarCartao(cartao));
        assertEquals("Os últimos 4 dígitos do número do cartão devem ser '1234'", thrown.getMessage());
    }

    @Test
    public void testSalvarCartao_InvalidCpf() {
        Cartao cartao = new Cartao();
        cartao.setId(1L);
        cartao.setCpf("111111sss1");
        cartao.setNumero("5200 1211 1435 1234");
        cartao.setData_validade("12/24");
        cartao.setCvv("123");

        // Act & Assert
        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () -> cartaoService.salvarCartao(cartao));
        assertEquals("400 : \"CPF inválido\"", thrown.getMessage());
    }

    @Test
    public void testBuscarCartaoPorId_Found() {
        // Arrange
        Long id = 1L;
        Cartao cartao = new Cartao();
        cartao.setId(id);

        when(cartaoRepository.findById(id)).thenReturn(Optional.of(cartao));

        // Act
        Cartao result = cartaoService.buscarCartaoPorId(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    public void testBuscarCartaoPorId_NotFound() {
        // Arrange
        Long id = 1L;

        when(cartaoRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> cartaoService.buscarCartaoPorId(id));
        assertEquals("Cartão não encontrado", thrown.getMessage());
    }

    @Test
    public void testConsultarCartao_Found() {
        // Arrange
        String cpf = "12345678900";
        Cartao cartao = new Cartao();
        cartao.setCpf(cpf);

        when(cartaoRepository.findByCpf(cpf)).thenReturn(Optional.of(cartao));

        // Act
        Cartao result = cartaoService.consultarCartao(cpf);

        // Assert
        assertNotNull(result);
        assertEquals(cpf, result.getCpf());
    }

    @Test
    public void testConsultarCartao_NotFound() {
        // Arrange
        Cartao cartao = new Cartao();
        cartao.setId(1L);
        cartao.setCpf("111111sss1");
        cartao.setNumero("5200 1211 1435 1234");
        cartao.setData_validade("12/24");
        cartao.setCvv("123");

        when(cartaoRepository.findByCpf(cartao.getCpf())).thenReturn(Optional.empty());

        // Act
        Cartao result = cartaoService.consultarCartao(cartao.getCpf());

        // Assert
        assertNull(result);
    }

    @Test
    public void testConsultarCPF_Success() {
        // Arrange
        Cartao cartao = new Cartao();
        cartao.setId(1L);
        cartao.setCpf("11111111111");
        cartao.setNumero("5200 1211 1435 1234");
        cartao.setData_validade("12/24");
        cartao.setCvv("123");

        ResponseEntity<String> response = new ResponseEntity<>("CPF válido", HttpStatus.OK);
        when(restTemplate.getForEntity(eq(String.format("http://localhost:8083/api/cliente/validarCPF/%s", cartao.getCpf())), eq(String.class)))
                .thenReturn(response);

        // Act
        boolean result = cartaoService.consultarCPF(cartao.getCpf());

        // Assert
        assertTrue(result);
    }

    @Test
    public void testConsultarCPFInválido() {
        // Arrange
        Cartao cartao = new Cartao();
        cartao.setId(1L);
        cartao.setCpf("111111112211");
        cartao.setNumero("5200 1211 1435 1234");
        cartao.setData_validade("12/24");
        cartao.setCvv("123");

        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "CPF inválido");
        when(restTemplate.getForEntity(eq(String.format("http://localhost:8083/api/cliente/validarCPF/%s", cartao.getCpf())), eq(String.class)))
                .thenThrow(exception);


        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () -> cartaoService.consultarCPF(cartao.getCpf()));
        assertEquals("400 : \"CPF inválido\"", thrown.getMessage());

      }


}