package br.com.fiap.hackaton_cartao_app.repository;

import br.com.fiap.hackaton_cartao_app.model.Cartao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartaoRepository extends JpaRepository<Cartao, Long> {
    Optional<Cartao> findByNumero(String numeroCartao);
    long countByCpf(String cpf);
    long countByNumero(String numero);

}
