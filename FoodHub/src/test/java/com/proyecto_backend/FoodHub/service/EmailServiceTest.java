package com.proyecto_backend.FoodHub.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEnviarCorreoBienvenida() {
        String destinatario = "usuario@correo.com";
        String nombreUsuario = "Juan";

        emailService.enviarCorreoBienvenida(destinatario, nombreUsuario);

        // Capturamos el mensaje que se envió
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage mensajeEnviado = messageCaptor.getValue();
        assertEquals(destinatario, mensajeEnviado.getTo()[0]);
        assertEquals("¡Bienvenido a UTEC Food Hub!", mensajeEnviado.getSubject());
        assertEquals("Hola Juan,\n\n" +
                "Gracias por registrarte en UTEC Food Hub, tu guía culinaria definitiva.\n\n" +
                "¡Esperamos que disfrutes de la plataforma!\n\n" +
                "Saludos,\nEl equipo de Food Hub", mensajeEnviado.getText());
    }
}
