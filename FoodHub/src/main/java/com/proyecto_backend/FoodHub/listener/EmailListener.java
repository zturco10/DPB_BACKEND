package com.proyecto_backend.FoodHub.listener;

import com.proyecto_backend.FoodHub.event.UsuarioRegistradoEvent;
import com.proyecto_backend.FoodHub.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async; // Importar @Async
import org.springframework.stereotype.Component;

@Component
public class EmailListener {

    private static final Logger logger = LoggerFactory.getLogger(EmailListener.class);

    @Autowired
    private EmailService emailService;


    @EventListener
    @Async
    public void handleUsuarioRegistradoEvent(UsuarioRegistradoEvent event) {
        logger.info("Evento UsuarioRegistradoEvent recibido para: {}", event.getEmail());

        emailService.enviarCorreoBienvenida(event.getEmail(), event.getNombre());
    }
}