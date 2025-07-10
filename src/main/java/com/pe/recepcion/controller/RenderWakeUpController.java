package com.pe.recepcion.controller;

import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@RestController
public class RenderWakeUpController {
    private Environment env;

    public RenderWakeUpController(Environment env) {
        this.env = env;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> pingRender() {
        return ResponseEntity.ok("ping levante no te duermaz perezoso working! ");
    }
    @GetMapping("/info-servidor-render")
    public Map<String, String> infoServidor() {
        Map<String,String> info = new HashMap<>();
        info.put("hora render", LocalDateTime.now().toString());
        info.put("zona render", TimeZone.getDefault().getID());
        return info;
    }

    @GetMapping("/info-render")
    public Map<String, Object> info() {
        Map<String, Object> data = new HashMap<>();
        long maxHeap = Runtime.getRuntime().maxMemory() / (1024 * 1024); // en MB
        long totalHeap = Runtime.getRuntime().totalMemory() / (1024 * 1024); // en MB

        data.put("heap.max.mb", maxHeap);
        data.put("heap.total.mb", totalHeap);
        data.put("activeProfiles", Arrays.toString(env.getActiveProfiles()));

        return data;
    }
}
