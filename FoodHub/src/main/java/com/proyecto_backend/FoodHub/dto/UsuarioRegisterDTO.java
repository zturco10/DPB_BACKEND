package com.proyecto_backend.FoodHub.dto;

import com.proyecto_backend.FoodHub.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsuarioRegisterDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Email(message = "Correo inválido")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;

    private Rol rol;
}
