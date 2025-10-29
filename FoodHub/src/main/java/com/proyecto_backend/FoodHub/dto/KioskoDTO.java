package com.proyecto_backend.FoodHub.dto;

import lombok.Data;

import java.util.List;

@Data
public class KioskoDTO {
    private Long id;
    private String nombre;
    private String ubicacion;
    private String horario;
    private List<Long> productoIds;
}
