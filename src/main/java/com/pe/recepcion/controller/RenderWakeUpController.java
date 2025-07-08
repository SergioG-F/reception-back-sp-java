package com.pe.recepcion.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RenderWakeUpController {
    @GetMapping("/ping")
    public ResponseEntity<String> pingRender() {
        return ResponseEntity.ok("ping levante no te duermaz perezoso working! ");
    }
}
