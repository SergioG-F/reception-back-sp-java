package com.pe.recepcion.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Data
@Getter
@Setter
public class ConfirmarAsistenciaRequest {
    private List<String> nombres; // Lista de invitados
    private Boolean asistio;
    private Integer cantidadGrupo; // opcional (usado si es un grupo)

    public List<String> getNombres() {
        return nombres;
    }

    public void setNombres(List<String> nombres) {
        this.nombres = nombres;
    }

    public Boolean getAsistio() {
        return asistio;
    }

    public void setAsistio(Boolean asistio) {
        this.asistio = asistio;
    }

    public Integer getCantidadGrupo() {
        return cantidadGrupo;
    }

    public void setCantidadGrupo(Integer cantidadGrupo) {
        this.cantidadGrupo = cantidadGrupo;
    }
}
