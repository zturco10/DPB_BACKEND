package com.proyecto_backend.FoodHub.dto;

import lombok.Data;

@Data
public class ResenaDTO {
    private Long id;
    private int calificacion;
    private String comentario;
    private Long usuarioId;
    private Long productoId;
}
