package com.ppay.apitransacaosimplificada.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transacao")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal valor;

    @JoinColumn(name = "recebedor_id")
    @ManyToOne
    private Usuario usuario;

    @JoinColumn(name = "pagador_id")
    @ManyToOne
    private Usuario pagador;

    private LocalDateTime dataHoraTransacao;

    @PrePersist
    void prePersist () {
        dataHoraTransacao = LocalDateTime.now();
    }

}
