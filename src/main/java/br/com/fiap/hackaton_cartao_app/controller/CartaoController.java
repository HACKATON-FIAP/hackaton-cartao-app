package br.com.fiap.hackaton_cartao_app.controller;

import br.com.fiap.hackaton_cartao_app.exception.CartaoException;
import br.com.fiap.hackaton_cartao_app.exception.CartaoLimitException;
import br.com.fiap.hackaton_cartao_app.model.Cartao;
import br.com.fiap.hackaton_cartao_app.service.CartaoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/cartao")
@CrossOrigin("*")
public class CartaoController {

    private CartaoService cartaoService;

    @PostMapping
    public ResponseEntity<Cartao> gerarCartao(@RequestBody Cartao cartao) {
        try {
            Cartao cartaoCriado = cartaoService.salvarCartao(cartao);
            return new ResponseEntity<>(cartaoCriado, HttpStatus.CREATED);
        } catch (CartaoLimitException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // Retorna 403 Forbidden para limite de cartões
        } catch (CartaoException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Retorna 400 Bad Request para número de cartão inválido
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Retorna 500 Internal Server Error para outros erros
        }
    }

    @GetMapping("/consultar/{cpf}")
    public ResponseEntity<Cartao> consultarCartao(@PathVariable String cpf) throws CartaoException {
        Cartao cartaoRetorno = cartaoService.consultarCartao(cpf);
        if (cartaoRetorno == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(cartaoRetorno, HttpStatus.OK);
        }
    }


    @PutMapping("/update")
    public ResponseEntity<Cartao> update(@RequestBody Cartao cartao) throws CartaoException {
        Optional<Cartao> cartaoAtual = cartaoService.updateLimiteCartao(cartao);
        return new ResponseEntity<>(cartaoAtual.orElse(null), HttpStatus.OK);
    }

}


