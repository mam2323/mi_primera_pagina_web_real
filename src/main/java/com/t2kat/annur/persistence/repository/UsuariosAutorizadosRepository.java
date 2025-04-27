package com.t2kat.annur.persistence.repository;


import com.t2kat.annur.persistence.model.UsuariosAutorizado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface UsuariosAutorizadosRepository

        extends JpaRepository<UsuariosAutorizado, Long> {

    UsuariosAutorizado findByUsuario(String usuario);

}
