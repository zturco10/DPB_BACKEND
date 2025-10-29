package com.proyecto_backend.FoodHub.event;

import org.springframework.context.ApplicationEvent;

public class NuevoProductoEvent extends ApplicationEvent {
    private final String nombreProducto;
    private final Long productoId;


    public NuevoProductoEvent(Object source, Long productoId, String nombreProducto) {
        super(source);
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public Long getProductoId() {
        return productoId;
    }
}