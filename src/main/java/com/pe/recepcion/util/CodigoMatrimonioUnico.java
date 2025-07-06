package com.pe.recepcion.util;

public class CodigoMatrimonioUnico {
    public static String generarCodigo(String prefijo) {
        int numero = (int)(Math.random() * 900000) + 100000; // Entre 100000 y 999999
        return prefijo + "-" + numero;
    }
}
