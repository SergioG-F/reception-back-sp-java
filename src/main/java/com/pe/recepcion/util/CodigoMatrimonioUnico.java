package com.pe.recepcion.util;

public class CodigoMatrimonioUnico {
    public static String generarCodigo(String prefijo) {
        int numero = (int)(Math.random() * 300) + 1; // Entre 1 y 300
        String numeroFormateado = String.format("%03d", numero); // → "001", "099", "300"
        return prefijo + "-" + numeroFormateado;

    }
}
