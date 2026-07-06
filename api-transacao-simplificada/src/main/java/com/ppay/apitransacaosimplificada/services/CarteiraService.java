package com.ppay.apitransacaosimplificada.services;

import com.ppay.apitransacaosimplificada.entities.Carteira;
import com.ppay.apitransacaosimplificada.reporitories.CarteiraRepository;
import com.ppay.apitransacaosimplificada.reporitories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarteiraService {

    private final CarteiraRepository carteiraRepository;

    public void salvar(Carteira carteira) {
        carteiraRepository.save(carteira);
    }
}
