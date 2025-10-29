package com.proyecto_backend.FoodHub.service;

import com.google.firebase.messaging.*;
import com.proyecto_backend.FoodHub.exception.NotificationSendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class FirebaseNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseNotificationService.class);


    @Async
    public void enviarNotificacionNuevoProducto(String nombreProducto, Long productoId) {

        String topic = "nuevos_productos";


        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle("¡Nuevo Producto en Food Hub!")
                        .setBody(nombreProducto + " ya está disponible. ¡Échale un vistazo!")
                        .build())

                .putData("productoId", String.valueOf(productoId))
                .putData("click_action", "FLUTTER_NOTIFICATION_CLICK")
                .setTopic(topic)
                .build();

        try {

            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Notificación push enviada exitosamente para producto '{}' al topic '{}': {}", nombreProducto, topic, response);
        } catch (FirebaseMessagingException e) {
            logger.error("Error al enviar notificación push para producto '{}' al topic '{}': {}", nombreProducto, topic, e.getMessage());

            throw new NotificationSendException("Error al enviar notificación push a Firebase: " + e.getMessage(), e);
        }
    }


}