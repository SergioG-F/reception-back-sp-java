package com.pe.recepcion.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class GenerationQrService {

    public void generarQRGeneral() throws IOException, WriterException {
        String url = "http://localhost:8082/confirmar-asistencia"; // URL pública para confirmar
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(url, BarcodeFormat.QR_CODE, 300, 300);

        Path path = Paths.get("qr");
        Files.createDirectories(path);

        Path archivo = path.resolve("qr-general.png");
        MatrixToImageWriter.writeToPath(matrix, "PNG", archivo);
    }

    public void generarQREntrada() throws IOException, WriterException {
        String url = "http://localhost:8082/marcar-entrada"; // URL pública para marcar entrada
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(url, BarcodeFormat.QR_CODE, 300, 300);

        Path path = Paths.get("qr");
        Files.createDirectories(path);

        Path archivo = path.resolve("qr-entrada.png");
        MatrixToImageWriter.writeToPath(matrix, "PNG", archivo);
    }
}
