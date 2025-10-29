package com.proyecto_backend.FoodHub.event;

import org.springframework.context.ApplicationEvent;

public class UsuarioRegistradoEvent extends ApplicationEvent {
    private final String email;
    private final String nombre;

    public UsuarioRegistradoEvent(Object source, String email, String nombre) {
        super(source);
        this.email = email;
        this.nombre = nombre;
    }


    public String getEmail() {
        return email;
    }

    public String getNombre() {
        return nombre;
    }
}