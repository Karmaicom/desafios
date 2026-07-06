package com.ppay.apitransacaosimplificada.dtos;

import java.math.BigDecimal;

public record TransacaoDTO(
        BigDecimal value,
        Long payer,
        Long payee
) {
}
