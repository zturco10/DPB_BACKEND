package com.proyecto_backend.FoodHub.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);
    @Value("${firebase.key-path}")
    private String firebaseKeyPath;

    @Bean
    public FirebaseApp initializeFirebase() {
        try {
            ClassPathResource resource = new ClassPathResource(firebaseKeyPath);
            InputStream serviceAccount = resource.getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) { // Evita inicializar múltiples veces
                FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);
                logger.info("Firebase Admin SDK inicializado correctamente.");
                return firebaseApp;
            } else {
                return FirebaseApp.getInstance();
            }
        } catch (IOException e) {
            logger.error("Error al inicializar Firebase Admin SDK: {}", e.getMessage());
            throw new RuntimeException("No se pudo inicializar Firebase", e);
        }
    }
}