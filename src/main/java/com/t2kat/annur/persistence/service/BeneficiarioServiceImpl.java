package com.t2kat.annur.persistence.service;


import com.t2kat.annur.persistence.model.Beneficiario;
import com.t2kat.annur.persistence.repository.BeneficiarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BeneficiarioServiceImpl implements BeneficiarioService {

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    public List<Beneficiario> listByTomadorId(long id) {
        return beneficiarioRepository.findBeneficiariosByTomadorId(id);
    }

    @Override
    public Beneficiario getBeneficiarioById(long id) {
        return beneficiarioRepository.findById(id).get();
    }
    @Override
    public void deleteBeneficiarioById(long id) {
        beneficiarioRepository.deleteById(id);
    }

    @Override
    public Page<Beneficiario> getMayoresDeEdad(String query,int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        LocalDate fechaLimite = LocalDate.now().minusYears(18);
        if (!query.isEmpty())
        return beneficiarioRepository.findBeneficiariosByNombreBeneficiarioOrApellidoBeneficiarioAndFechaNacimientoBeforeAndParentesco(query,query,fechaLimite,2,pageable);
        else
        return beneficiarioRepository.findBeneficiariosByFechaNacimientoBeforeAndParentesco(fechaLimite,2,pageable);

    }
    @Override
    public Beneficiario saveBeneficiario(Beneficiario beneficiario) {
        return beneficiarioRepository.save(beneficiario);
    }
}
