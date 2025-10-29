package com.proyecto_backend.FoodHub.service;

import com.proyecto_backend.FoodHub.dto.ResenaDTO;
import com.proyecto_backend.FoodHub.exception.AccesoNoAutorizadoException;
import com.proyecto_backend.FoodHub.exception.ResourceNotFoundException;
import com.proyecto_backend.FoodHub.model.Producto;
import com.proyecto_backend.FoodHub.model.Resena;
import com.proyecto_backend.FoodHub.model.Rol;
import com.proyecto_backend.FoodHub.model.Usuario;
import com.proyecto_backend.FoodHub.repository.ProductoRepository;
import com.proyecto_backend.FoodHub.repository.ResenaRepository;
import com.proyecto_backend.FoodHub.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResenaService {

    @Autowired
    private ResenaRepository resenaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ProductoRepository productoRepository;

    public ResenaDTO guardarResena(ResenaDTO dto) {
        Resena resena;
        Usuario usuarioAutenticado = getUsuarioAutenticado();
        if (dto.getId() != null) {
            resena = resenaRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada con ID: " + dto.getId()));
            validarPropietario(resena, usuarioAutenticado);
        } else {
            resena = new Resena();
        }

        resena.setCalificacion(dto.getCalificacion());
        resena.setComentario(dto.getComentario());

        // Asignar Usuario
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado para la reseña: " + dto.getUsuarioId()));
        resena.setUsuario(usuario);

        // Asignar Producto
        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado para la reseña: " + dto.getProductoId()));
        resena.setProducto(producto);

        Resena guardada = resenaRepository.save(resena);
        return convertirADTO(guardada);
    }

    public List<ResenaDTO> obtenerTodas() {
        return resenaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public ResenaDTO obtenerPorId(Long id) {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada con ID: " + id));
        return convertirADTO(resena);
    }

    public void eliminarResena(Long id) {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar. Reseña no existe con ID: " + id));
        Usuario usuarioAutenticado = getUsuarioAutenticado();
        validarPropietario(resena, usuarioAutenticado);
        resenaRepository.deleteById(id);
    }

    private Usuario getUsuarioAutenticado() {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado en contexto de seguridad"));
    }

    private void validarPropietario(Resena resena, Usuario usuario) {
        if (usuario.getRol() == Rol.ADMIN) {
            return; // Admin puede borrar cualquier reseña
        }

        if (resena.getUsuario() == null || !resena.getUsuario().getId().equals(usuario.getId())) {
            throw new AccesoNoAutorizadoException("No tienes permiso para modificar esta reseña.");
        }
    }

    public List<ResenaDTO> obtenerPorProducto(Long productoId) {
        if (!productoRepository.existsById(productoId)) {
            throw new ResourceNotFoundException("Producto no existe con ID: " + productoId);
        }
        return resenaRepository.findByProductoId(productoId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<ResenaDTO> obtenerPorUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuario no existe con ID: " + usuarioId);
        }
        return resenaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<ResenaDTO> obtenerPorCalificacion(int calificacion) {
        return resenaRepository.findByCalificacion(calificacion).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // --- Helper de Conversión ---
    private ResenaDTO convertirADTO(Resena resena) {
        ResenaDTO dto = new ResenaDTO();
        dto.setId(resena.getId());
        dto.setCalificacion(resena.getCalificacion());
        dto.setComentario(resena.getComentario());
        dto.setUsuarioId(resena.getUsuario().getId());
        dto.setProductoId(resena.getProducto().getId());
        return dto;
    }
}