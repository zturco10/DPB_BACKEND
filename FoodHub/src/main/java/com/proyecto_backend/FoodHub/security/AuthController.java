package com.proyecto_backend.FoodHub.security;

import com.proyecto_backend.FoodHub.dto.AuthRequest;
import com.proyecto_backend.FoodHub.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Importar
import org.springframework.http.ResponseEntity; // Importar
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth") // ✅ Versionado
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    // ✅ Devuelve 201 Created
    public ResponseEntity<String> register(@RequestBody Usuario usuario) {
        String token = authService.register(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

    @PostMapping("/login")
    // (Login se queda con 200 OK)
    public String login(@RequestBody AuthRequest request) {
        return authService.authenticate(request.getCorreo(), request.getContrasena());
    }
}