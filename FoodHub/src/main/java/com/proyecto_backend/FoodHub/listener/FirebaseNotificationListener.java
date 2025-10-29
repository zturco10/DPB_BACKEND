package com.proyecto_backend.FoodHub.listener;

import com.proyecto_backend.FoodHub.event.NuevoProductoEvent;
import com.proyecto_backend.FoodHub.service.FirebaseNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class FirebaseNotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseNotificationListener.class);

    @Autowired
    private FirebaseNotificationService notificationService;

    @EventListener
    @Async
    public void handleNuevoProductoEvent(NuevoProductoEvent event) {
        logger.info("Evento NuevoProductoEvent recibido para producto: {}", event.getNombreProducto());

        notificationService.enviarNotificacionNuevoProducto(event.getNombreProducto(), event.getProductoId());
    }
}