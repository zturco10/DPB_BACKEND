package com.proyecto_backend.FoodHub.security;

import com.proyecto_backend.FoodHub.event.UsuarioRegistradoEvent;
import com.proyecto_backend.FoodHub.model.Usuario;
import com.proyecto_backend.FoodHub.exception.InvalidCredentialsException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import com.proyecto_backend.FoodHub.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional // ✅ 5. Hacer el método transaccional
    public String register(Usuario usuario) {
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        UsuarioRegistradoEvent evento = new UsuarioRegistradoEvent(this, usuarioGuardado.getCorreo(), usuarioGuardado.getNombre());
        eventPublisher.publishEvent(evento);

        return jwtUtil.generateToken(usuarioGuardado.getCorreo());
    }

    public String authenticate(String correo, String contrasena) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(correo, contrasena));
            return jwtUtil.generateToken(correo);
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Correo o contraseña incorrectos.", e);
        }
    }
}