package com.ppay.apitransacaosimplificada.services;

import com.ppay.apitransacaosimplificada.infraestructure.clients.AutorizacaoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutorizacaoService {

    private final AutorizacaoClient autorizacaoClient;

    public boolean validarTransferencia() {
        return autorizacaoClient.validarAutorizacao().data().authorization().equals("true");
    }

}
