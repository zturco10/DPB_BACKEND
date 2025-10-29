package com.proyecto_backend.FoodHub.dto;

import com.proyecto_backend.FoodHub.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsuarioDTO {

    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @Email(message = "Correo inválido")
    private String correo;

    private Rol rol;
}
