package com.ppay.apitransacaosimplificada.reporitories;

import com.ppay.apitransacaosimplificada.entities.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

}
