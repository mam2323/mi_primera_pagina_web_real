package com.t2kat.annur.util;

import java.time.LocalDate;
import java.time.Period;

public class Utilidades {
    public static int calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser nula");
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }


    public static String getEstado(int estado) {
        return switch (estado) {
            case 1 -> "Soltero";
            case 2 -> "Casado";
            case 3 -> "Divorciado";
            case 4 -> "Viudo";
            default -> "Desconocido";
        };
    }


    public static String getParentesco(int parentesco) {
        return switch (parentesco) {
            case 1 -> "Cónyuge";
            case 2 -> "Hijo/a soltero ";
            case 3 -> "Hija ";
            case 4 -> "Padre/Madre";
            default -> "Otro";
        };
    }

}
