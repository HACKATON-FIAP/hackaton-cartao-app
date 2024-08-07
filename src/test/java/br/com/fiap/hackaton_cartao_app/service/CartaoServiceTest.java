package br.com.fiap.hackaton_cartao_app.service;

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
    public void testConsultarCPF_Success() {
        // Arrange
        String cpf = "11111111111";
        String apiClientUrl = "http://localhost:8083/api/cliente/validarCPF/";
        String url = apiClientUrl + cpf;

        ResponseEntity<String> response = new ResponseEntity<>("", HttpStatus.OK);
        when(restTemplate.getForEntity(url, String.class)).thenReturn(response);

        // Act
        boolean result = cartaoService.consultarCPF(cpf);

        // Assert
        assertTrue(result);
    }


    @Test
    public void testConsultarCPF_BadRequest() {
        // Arrange
        String cpf = "12345678900";
        String url = String.format("http://localhost:8083/api/cliente/validarCPF/%s", cpf);

        // Simulate HttpClientErrorException with 400 Bad Request
        ResponseEntity<String> response = new ResponseEntity<>("", HttpStatus.BAD_REQUEST); // Simulate error response
        when(restTemplate.getForEntity(url, String.class)).thenReturn(response);

        // Act
        boolean result = cartaoService.consultarCPF(cpf);

        // Assert
        assertFalse(result); // Expect false for error response
    }

    @Test
    public void testConsultarCPF_NotFound() {
        // Arrange
        String cpf = "12345678900";
        String url = String.format("http://localhost:8083/api/cliente/validarCPF/%s", cpf);

        // Simulate HttpClientErrorException with 404 Not Found
        ResponseEntity<String> response = new ResponseEntity<>("", HttpStatus.NOT_FOUND); // Simulate error response
        when(restTemplate.getForEntity(url, String.class)).thenReturn(response);

        // Act
        boolean result = cartaoService.consultarCPF(cpf);

        // Assert
        assertFalse(result); // Expect false for error response
    }


}