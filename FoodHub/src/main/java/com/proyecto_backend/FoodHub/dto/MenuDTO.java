package com.proyecto_backend.FoodHub.dto;

import lombok.Data;

import java.util.List;

@Data
public class MenuDTO {
    private Long id;
    private String tipo;
    private String dia;
    private double precio;
    private List<Long> productoIds;
}
