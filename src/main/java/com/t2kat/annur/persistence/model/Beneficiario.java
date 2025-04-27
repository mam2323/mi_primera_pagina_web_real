package com.t2kat.annur.persistence.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "beneficiarios")
public class Beneficiario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "Tomador_id")
    private Long tomadorId;


    @Column(name = "nombre_Tomador")
    private String nombreTomador;

    @Column(name = "apellido_Tomador")
    private String apellidoTomador;

    @Column(name = "nombre_beneficiario")
    private String nombreBeneficiario;

    @Column(name = "apellido_beneficiario")
    private String apellidoBeneficiario;

    @Column(name = "identificacion_beneficiario")
    private String identificacionBeneficiario;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "parentesco")
    private Integer parentesco;

    @Column(name = "observacion")
    private String observacion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getTomadorId() {
        return tomadorId;
    }

    public void setTomadorId(Long tomadorId) {
        this.tomadorId = tomadorId;
    }

    public String getNombreTomador() {
        return nombreTomador;
    }

    public void setNombreTomador(String nombreTomador) {
        this.nombreTomador = nombreTomador;
    }

    public String getApellidoTomador() {
        return apellidoTomador;
    }

    public void setApellidoTomador(String apellidoTomador) {
        this.apellidoTomador = apellidoTomador;
    }

    public String getNombreBeneficiario() {
        return nombreBeneficiario;
    }

    public void setNombreBeneficiario(String nombreBeneficiario) {
        this.nombreBeneficiario = nombreBeneficiario;
    }

    public String getApellidoBeneficiario() {
        return apellidoBeneficiario;
    }

    public void setApellidoBeneficiario(String apellidoBeneficiario) {
        this.apellidoBeneficiario = apellidoBeneficiario;
    }

    public String getIdentificacionBeneficiario() {
        return identificacionBeneficiario;
    }

    public void setIdentificacionBeneficiario(String identificacionBeneficiario) {
        this.identificacionBeneficiario = identificacionBeneficiario;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Integer getParentesco() {
        return parentesco;
    }

    public void setParentesco(Integer parentesco) {
        this.parentesco = parentesco;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

}