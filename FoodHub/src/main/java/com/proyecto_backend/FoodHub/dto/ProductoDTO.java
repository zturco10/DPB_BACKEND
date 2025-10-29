package com.proyecto_backend.FoodHub.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductoDTO {
    private Long id;
    private String nombre;
    private double precio;
    private String descripcion;

    private String imageUrl;

    private Long vendedorId;
    private List<Long> categoriaIds;
    private Long menuId;
    private List<Long> kioskoIds;
}
