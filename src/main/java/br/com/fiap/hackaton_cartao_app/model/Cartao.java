package br.com.fiap.hackaton_cartao_app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Cartao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
    private String cpf;

    @NotBlank(message = "Limite é obrigatório")
    private Long limite;

    @NotBlank(message = "Cartão é obrigatório")
    @Pattern(regexp = "d{4} \\d{4} \\d{4} 1234", message = "Número do cartão deve seguir o formato: **** **** **** 1234")
    private String numero;

    @NotBlank(message = "Data de validade é obrigatório")
    @Pattern(regexp = "d{2}/\\d{2}", message = "Data de validade no formato: 12/24")
    private String data_validade;

    @NotBlank(message = "Código de verificação é obrigatório")
    @Pattern(regexp = "d{3}", message = "Código de verificação no formato: 123")
    private String cvv;

    /*

        Cartao cartao = new Cartao();
        cartao.setCpf("12345678901");
        cartao.setLimite("1000.00");
        cartao.setNumeroCartao("1234 5678 1234 5678");
        cartao.setDataValidade("12/24");
        cartao.setCodigoVerificacao("123");
     */

}
