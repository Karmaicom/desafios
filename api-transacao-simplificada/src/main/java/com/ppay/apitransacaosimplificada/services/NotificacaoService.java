package com.ppay.apitransacaosimplificada.services;

import com.ppay.apitransacaosimplificada.infraestructure.clients.NotificacaoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoClient notificacaoClient;

    public void enviarNotificacao() {
        notificacaoClient.enviarNotificacao();
    }
}
