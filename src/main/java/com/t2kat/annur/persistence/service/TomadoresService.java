package com.t2kat.annur.persistence.service;


import com.t2kat.annur.persistence.model.Tomadores;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TomadoresService {

    public List<Tomadores> list();
    public Tomadores getTomadorById(long id);

    public void deleteTomadorById(long id);

    public Page<Tomadores> listPaginated(int page, int size);
    public Tomadores saveTomador(Tomadores tomador) ;
    //public Page<Tomadores> buscarPorDniONombre(String query, int page, int size);
    Page<Tomadores> buscarPorDniONombrePolizaCaducada(String query,
                                                      boolean polizaCaducada,
                                                      Boolean verAvisados,
                                                      Boolean verProximosAVencer,
                                                      int page,
                                                      int size);



    void generarFichaPDF(Long id, HttpServletResponse response);

    void enviarAvisosMasivos(boolean reenviar, boolean proximosAVencer);

}
