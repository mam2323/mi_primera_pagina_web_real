package com.t2kat.annur.persistence.service;


import com.t2kat.annur.persistence.model.UsuariosAutorizado;
import com.t2kat.annur.persistence.repository.UsuariosAutorizadosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class UsuarioAutorizadoServiceImpl implements UsuarioAutorizadoService {
    @Override
    public UsuariosAutorizado findByUsuario(String usuario) {
        return usuariosAutorizadosRepository.findByUsuario(usuario);
    }

    @Autowired
    private UsuariosAutorizadosRepository usuariosAutorizadosRepository;
}
