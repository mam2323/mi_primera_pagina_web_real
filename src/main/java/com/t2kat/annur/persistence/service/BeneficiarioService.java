package com.t2kat.annur.persistence.service;


import com.t2kat.annur.persistence.model.Beneficiario;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BeneficiarioService {

    public List<Beneficiario> listByTomadorId(long id);
    public Beneficiario getBeneficiarioById(long id);

    public void deleteBeneficiarioById(long id);

    public Page<Beneficiario> getMayoresDeEdad(String query,int page, int size);
    public Beneficiario saveBeneficiario(Beneficiario beneficiario) ;


}
