package com.ppay.apitransacaosimplificada.services;

import com.ppay.apitransacaosimplificada.dtos.TransacaoDTO;
import com.ppay.apitransacaosimplificada.entities.Carteira;
import com.ppay.apitransacaosimplificada.entities.Transacao;
import com.ppay.apitransacaosimplificada.entities.Usuario;
import com.ppay.apitransacaosimplificada.enums.TipoUsuario;
import com.ppay.apitransacaosimplificada.exceptions.BadRequestException;
import com.ppay.apitransacaosimplificada.reporitories.TransacaoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final UsuarioService usuarioService;
    private final AutorizacaoService autorizacaoService;
    private final CarteiraService carteiraService;
    private final NotificacaoService notificacaoService;
    private final TransacaoRepository transacaoRepository;

    @Transactional
    public void transferirValores(TransacaoDTO transacaoDTO) {
        //Buscar usuario pagador
        var pagador = usuarioService.buscarPorId(transacaoDTO.payer());

        //Buscar usuario recebedor
        var recebedor = usuarioService.buscarPorId(transacaoDTO.payee());

        // Validando se o usuario é lojista.
        // Lojista não pode fazer transferências
        validaPagadorLojista(pagador);

        // Validar se o saldo é suficiente
        validarSaldoUsuario(pagador, transacaoDTO.value());

        // Validar transferencia (autorizacao)
        validarTransferencia();

        // Atualizando saldo do pagador
        pagador.getCarteira().setSaldo(pagador.getCarteira().getSaldo().subtract(transacaoDTO.value()));
        atualizarSadoCarteira(pagador.getCarteira());

        // Atualizando saldo do recebedor
        recebedor.getCarteira().setSaldo(recebedor.getCarteira().getSaldo().add(transacaoDTO.value()));
        atualizarSadoCarteira(recebedor.getCarteira());

        var transacoes = Transacao.builder()
                .valor(transacaoDTO.value())
                .usuario(recebedor)
                .pagador(pagador)
                .build();

        // Salvar transacoes no banco de dados
        transacaoRepository.save(transacoes);

        // Envia notificacao
        enviarNotificacao();
    }

    private void validaPagadorLojista(Usuario usuario) {
        try {
            if (usuario.getTipoUsuario().equals(TipoUsuario.LOJISTA))
                throw new IllegalArgumentException("Usuário do tipo lojista não pode realizar transferências");

        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao validar o tipo de usuário: " + e.getMessage());
        }
    }

    private void validarSaldoUsuario(Usuario usuario, BigDecimal valor) {
        try {
            if (usuario.getCarteira().getSaldo().compareTo(valor) < 0)
                throw new IllegalArgumentException("Saldo insuficiente para realizar a transferência");

        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao validar o saldo do usuário: " + e.getMessage());
        }
    }

    private void validarTransferencia() {
        try {
            if (!autorizacaoService.validarTransferencia())
                throw new IllegalArgumentException("Transferência não autorizada");

        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao validar a transferência: " + e.getMessage());
        }
    }

    private void atualizarSadoCarteira(Carteira carteira) {
        carteiraService.salvar(carteira);
    }

    private void enviarNotificacao() {
        try {
            notificacaoService.enviarNotificacao();
        }  catch (HttpClientErrorException e) {
            throw new BadRequestException("Erro ao enviar notificação: " + e.getMessage());
        }
    }

}
