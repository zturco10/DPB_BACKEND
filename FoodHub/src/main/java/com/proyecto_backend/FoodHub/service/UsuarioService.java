package com.proyecto_backend.FoodHub.service;

import com.proyecto_backend.FoodHub.exception.UserAlreadyExistsException;
import com.proyecto_backend.FoodHub.exception.UserNotFoundException;
import com.proyecto_backend.FoodHub.model.Usuario;
import com.proyecto_backend.FoodHub.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("No se encontró el usuario con ID: " + id));
    }

    public Usuario guardar(Usuario usuario) {
        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new UserAlreadyExistsException("Ya existe un usuario con el correo: " + usuario.getCorreo());
        }
        return usuarioRepository.save(usuario);
    }


    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new UserNotFoundException("No se puede eliminar. El usuario con ID " + id + " no existe.");
        }
        usuarioRepository.deleteById(id);
    }

    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con correo: " + correo));

    }

}
