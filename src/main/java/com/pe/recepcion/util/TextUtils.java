package com.pe.recepcion.util;

import java.text.Normalizer;

public class TextUtils {
    public static String normalizar(String texto) {
        if (texto == null) return null;
        String noAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return noAcentos.toLowerCase().trim();
    }
}
