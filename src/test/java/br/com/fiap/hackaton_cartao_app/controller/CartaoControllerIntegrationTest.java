package br.com.fiap.hackaton_cartao_app.controller;


import br.com.fiap.hackaton_cartao_app.model.Cartao;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
@AutoConfigureMockMvc
public class CartaoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    @Autowired
    private CartaoController cartaoController;


    @Test
    public void shouldCreateCartao() throws Exception {
        Cartao cartao = new Cartao();
        cartao.setId(1L);
        cartao.setCpf("11111111111");
        cartao.setNumero("5200 1211 1435 1234");
        cartao.setData_validade("12/24");
        cartao.setCvv("123");
        String cartaoJson = objectMapper.writeValueAsString(cartao);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/cartao")
                        .contentType("application/json")
                        .content(cartaoJson))
                .andExpect(status().isCreated());

    }

    @Test
    public void shouldReturnBadRequestForInvalidCartao() throws Exception {

        Cartao invalidCartao = new Cartao();

        invalidCartao.setId(1L);
        invalidCartao.setCpf("11111111111");
        invalidCartao.setNumero("5200 1211 1435 4444");
        invalidCartao.setData_validade("12/24");
        invalidCartao.setCvv("123");

        String cartaoJson = objectMapper.writeValueAsString(invalidCartao);


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/cartao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cartaoJson))
                .andExpect(status().is5xxServerError())
                .andReturn();


        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Response: " + responseContent);

        assertNotNull(responseContent);
    }

    @Test
    public void shouldReturn500ForOtherErrors() throws Exception {

        Cartao invalidCartao = new Cartao();

        invalidCartao.setId(1L);
        invalidCartao.setCpf("111111111");
        invalidCartao.setNumero("5200 1211 1435 1234");
        invalidCartao.setData_validade("12/24");
        invalidCartao.setCvv("123");

        String cartaoJson = objectMapper.writeValueAsString(invalidCartao);


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/cartao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cartaoJson))
                .andExpect(status().is5xxServerError())
                .andReturn();


        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Response: " + responseContent);

        assertNotNull(responseContent);
    }



    @Test
    public void testConsultarCartao_Success() throws Exception {
        Cartao cartao = new Cartao();
        cartao.setId(1L);
        cartao.setCpf("11111111111");
        cartao.setNumero("5200 1211 1435 1234");
        cartao.setData_validade("12/24");
        cartao.setCvv("123");
        cartaoController.gerarCartao(cartao);

        String cpf = "11111111111";
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cartao/consultar/{cpf}", cpf)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testConsultarCartao_NotFound() throws Exception {
        String cpf = "00000000000";
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cartao/consultar/{cpf}", cpf)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }



}