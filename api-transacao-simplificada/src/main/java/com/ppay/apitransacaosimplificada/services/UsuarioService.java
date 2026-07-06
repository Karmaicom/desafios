package com.ppay.apitransacaosimplificada.services;

import com.ppay.apitransacaosimplificada.entities.Usuario;
import com.ppay.apitransacaosimplificada.exceptions.UserNotFoundException;
import com.ppay.apitransacaosimplificada.reporitories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository  usuarioRepository;

    public Usuario buscarPorId(Long id) {
        return usuarioRepository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado!"));
    }

}
