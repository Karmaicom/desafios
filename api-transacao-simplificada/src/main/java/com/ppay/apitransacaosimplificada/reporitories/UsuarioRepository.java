package com.ppay.apitransacaosimplificada.reporitories;

import com.ppay.apitransacaosimplificada.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
