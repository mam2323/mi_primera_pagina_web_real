package com.t2kat.annur.persistence.repository;


import com.t2kat.annur.persistence.model.Tomadores;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;


@Repository

public interface TomadoresRepository

        extends JpaRepository<Tomadores, Long> {

    Page<Tomadores>  findByNombreOrApellidoOrIdentificacionOrTelefonoOrDireccion(String nombre,String apellido,String identificacion , String telefono, String direccion,Pageable pageable);
    Page<Tomadores>  findByNombreOrApellidoOrIdentificacionOrTelefonoOrDireccionAndFechaFinBefore(String nombre, String apellido, String identificacion , String telefono, String direccion, LocalDate fechaFin, Pageable pageable);
    Page<Tomadores>  findByFechaFinBefore( LocalDate fechaFin, Pageable pageable);
    // 🔥 Caducados y avisados
    Page<Tomadores> findByFechaFinBeforeAndAvisoEnviadoTrue(LocalDate fecha, Pageable pageable);

    // 🔥 Caducados y no avisados
    Page<Tomadores> findByFechaFinBeforeAndAvisoEnviadoFalse(LocalDate fecha, Pageable pageable);

    // ⏳ Próximos a vencer
    Page<Tomadores> findByFechaFinBetween(LocalDate desde, LocalDate hasta, Pageable pageable);

    // ⏳ Próximos a vencer y ya avisados
    Page<Tomadores> findByFechaFinBetweenAndAvisoEnviadoTrue(LocalDate desde, LocalDate hasta, Pageable pageable);

    // ⏳ Próximos a vencer y no avisados
    Page<Tomadores> findByFechaFinBetweenAndAvisoEnviadoFalse(LocalDate desde, LocalDate hasta, Pageable pageable);

}




// Métodos heredados automáticamente de JpaRepository:

// List<Tomadores> findAll();                        // Devuelve todos los registros
// Optional<Tomadores> findById(Long id);            // Busca un registro por su ID
// Tomadores save(Tomadores entidad);                // Guarda o actualiza un registro
// void deleteById(Long id);                         // Borra un registro por su ID
// boolean existsById(Long id);                      // Verifica si existe un registro
// long count();                                     // Cuenta cuántos registros hay
// List<Tomadores> findAllById(Iterable<Long> ids);  // Busca varios por ID
// void delete(Tomadores entidad);                   // Borra una entidad completa
// void deleteAll();                                 // Borra todos los registros
// void deleteAll(Iterable<? extends Tomadores> entidades); // Borra varios a la vez
