package com.t2kat.annur.persistence.service;


import com.t2kat.annur.persistence.repository.UsuariosAutorizadosRepository;
import com.t2kat.annur.persistence.model.UsuariosAutorizado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
public interface UsuarioAutorizadoService {

    UsuariosAutorizado findByUsuario(String usuario);

}
