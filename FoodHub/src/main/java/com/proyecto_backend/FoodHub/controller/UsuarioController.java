package com.proyecto_backend.FoodHub.controller;

import com.proyecto_backend.FoodHub.dto.UsuarioDTO;
import com.proyecto_backend.FoodHub.dto.UsuarioRegisterDTO;
import com.proyecto_backend.FoodHub.model.Usuario;
import com.proyecto_backend.FoodHub.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // ✅ 1. Listar todos los usuarios (convertimos manualmente dentro del método)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        List<UsuarioDTO> usuarios = usuarioService.listarTodos().stream()
                .map(usuario -> {
                    UsuarioDTO dto = new UsuarioDTO();
                    dto.setId(usuario.getId());
                    dto.setNombre(usuario.getNombre());
                    dto.setCorreo(usuario.getCorreo());
                    dto.setRol(usuario.getRol());
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(usuarios);
    }

    // ✅ 2. Buscar usuario por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);

        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setCorreo(usuario.getCorreo());
        dto.setRol(usuario.getRol());

        return ResponseEntity.ok(dto);
    }

    // ✅ 3. Buscar usuario por correo
    @GetMapping("/correo/{correo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioDTO> buscarPorCorreo(@PathVariable String correo) {
        Usuario usuario = usuarioService.buscarPorCorreo(correo);

        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setCorreo(usuario.getCorreo());
        dto.setRol(usuario.getRol());

        return ResponseEntity.ok(dto);
    }

    // ✅ 4. Crear un nuevo usuario
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioDTO> crear(@RequestBody @Valid UsuarioRegisterDTO dto) {
        Usuario nuevo = new Usuario();
        nuevo.setNombre(dto.getNombre());
        nuevo.setCorreo(dto.getCorreo());
        nuevo.setContrasena(dto.getContrasena());
        nuevo.setRol(dto.getRol());

        Usuario guardado = usuarioService.guardar(nuevo);

        UsuarioDTO respuesta = new UsuarioDTO();
        respuesta.setId(guardado.getId());
        respuesta.setNombre(guardado.getNombre());
        respuesta.setCorreo(guardado.getCorreo());
        respuesta.setRol(guardado.getRol());

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    // ✅ 5. Actualizar usuario existente
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioDTO> actualizar(@PathVariable Long id, @RequestBody UsuarioRegisterDTO dto) {
        Usuario existente = usuarioService.buscarPorId(id);

        existente.setNombre(dto.getNombre());
        existente.setCorreo(dto.getCorreo());
        existente.setContrasena(dto.getContrasena());
        existente.setRol(dto.getRol());

        Usuario actualizado = usuarioService.guardar(existente);

        UsuarioDTO respuesta = new UsuarioDTO();
        respuesta.setId(actualizado.getId());
        respuesta.setNombre(actualizado.getNombre());
        respuesta.setCorreo(actualizado.getCorreo());
        respuesta.setRol(actualizado.getRol());

        return ResponseEntity.ok(respuesta);
    }

    // ✅ 6. Eliminar usuario
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
