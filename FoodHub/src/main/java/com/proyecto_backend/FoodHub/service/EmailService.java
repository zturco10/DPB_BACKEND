package com.proyecto_backend.FoodHub.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async; // Importar @Async
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;


    @Async
    public void enviarCorreoBienvenida(String destinatario, String nombreUsuario) {
        logger.info("Intentando enviar correo de bienvenida a: {}", destinatario);
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(destinatario);
            mensaje.setSubject("¡Bienvenido a UTEC Food Hub!");
            mensaje.setText("Hola " + nombreUsuario + ",\n\n" +
                    "Gracias por registrarte en UTEC Food Hub, tu guía culinaria definitiva.\n\n" +
                    "¡Esperamos que disfrutes de la plataforma!\n\n" +
                    "Saludos,\nEl equipo de Food Hub");



            mailSender.send(mensaje);
            logger.info("Correo de bienvenida enviado exitosamente a: {}", destinatario);
        } catch (Exception e) {

            logger.error("Error al enviar correo de bienvenida a {}: {}", destinatario, e.getMessage());
        }
    }


}