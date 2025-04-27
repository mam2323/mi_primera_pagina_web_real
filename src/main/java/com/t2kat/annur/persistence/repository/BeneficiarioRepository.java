package com.t2kat.annur.persistence.repository;


import com.t2kat.annur.persistence.model.Beneficiario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository

public interface BeneficiarioRepository

        extends JpaRepository<Beneficiario, Long> {

    List<Beneficiario> findBeneficiariosByTomadorId(long tomadorId);
    Page<Beneficiario> findBeneficiariosByNombreBeneficiarioOrApellidoBeneficiarioAndFechaNacimientoBeforeAndParentesco(String nombre,String apellido,  LocalDate date, int parentesco, Pageable page);
    Page<Beneficiario> findBeneficiariosByFechaNacimientoBeforeAndParentesco( LocalDate date,int parentesco, Pageable page);

}
