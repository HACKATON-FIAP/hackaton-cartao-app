package br.com.fiap.hackaton_cartao_app.service;

import br.com.fiap.hackaton_cartao_app.exception.CartaoException;
import br.com.fiap.hackaton_cartao_app.exception.CartaoLimitException;
import br.com.fiap.hackaton_cartao_app.model.Cartao;
import br.com.fiap.hackaton_cartao_app.repository.CartaoRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CartaoService {

    @Autowired
    private CartaoRepository cartaoRepository;

    private static final int MAX_CARTOES_POR_CPF = 2;
    private static final String REQUISITO_ULTIMOS_DIGITOS = "1234";

    private final String apiClientUrl;

    @Autowired
    public CartaoService(@Value("${api.key}") String apiClientUrl) {
        this.apiClientUrl = apiClientUrl;
    }

    public Cartao salvarCartao(Cartao cartao) {
        //Verificar se CPF existe ao cadastrar cartão
        boolean statusCPF = consultarCPF(cartao.getCpf());
        if (statusCPF) {


            // Verificar o número de cartões existentes para o CPF
            long count = cartaoRepository.countByCpf(cartao.getCpf());
            if (count >= MAX_CARTOES_POR_CPF) {
                throw new CartaoLimitException("Não é permitido ter mais de dois cartões para o mesmo CPF");
            }


            // Verificar se já existe um cartão com o mesmo número
            long countNumero = cartaoRepository.countByNumero(cartao.getNumero());
            if (countNumero > 0) {
                throw new CartaoException("Não é permitido dois cartões com o mesmo número");
            }

            // Verificar se os últimos 4 dígitos do número do cartão são "1234"
            if (!verificarUltimosDigitos(cartao.getNumero())) {
                throw new CartaoException("Os últimos 4 dígitos do número do cartão devem ser '1234'");
            }

            // Formatar número do cartão antes de salvar
            String numeroCartaoFormatado = formatarNumeroCartao(cartao.getNumero());
            cartao.setNumero(numeroCartaoFormatado);

            return cartaoRepository.save(cartao);
        } else {
            throw new CartaoException("Não é permitido operação de cartão com CPF inválido!");
        }


    }

    public boolean verificarUltimosDigitos(String numeroCartao) {
        // Remove espaços e formata o número do cartão
        String apenasDigitos = numeroCartao.replaceAll("\\D", "");
        // Verifica os últimos 4 dígitos
        return apenasDigitos.endsWith(REQUISITO_ULTIMOS_DIGITOS);
    }

    public Cartao buscarCartaoPorId(Long id) {
        return cartaoRepository.findById(id).orElseThrow(() -> new RuntimeException("Cartão não encontrado"));
    }

    private String formatarNumeroCartao(String numeroCartao) {
        // Remove espaços e formata o número do cartão
        String apenasDigitos = numeroCartao.replaceAll("\\D", "");
        return apenasDigitos.replaceAll("(\\d{4})(\\d{4})(\\d{4})(\\d{4})", "$1 $2 $3 $4");
    }

    public boolean consultarCPF(String cpf) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> retornoCPF = restTemplate.getForEntity(String.format("http://localhost:8083/api/cliente/validarCPF/%s", cpf), String.class);
        if (retornoCPF.getStatusCode().is2xxSuccessful()) {
            return true;
        } else {
            return false;
        }
    }

    public Cartao consultarCartao(String cpf){
        Optional<Cartao> cartao = cartaoRepository.findByCpf(cpf);
        return cartao.orElse(null);
    }



}
