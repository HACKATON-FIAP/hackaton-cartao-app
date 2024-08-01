package br.com.fiap.hackaton_cartao_app.exception;

public class CartaoLimitException extends RuntimeException {
    public CartaoLimitException(String message) {
        super(message);
    }
}
