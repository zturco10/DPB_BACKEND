package com.proyecto_backend.FoodHub.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String correo;
    private String contrasena;
}
