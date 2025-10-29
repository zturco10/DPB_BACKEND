package com.proyecto_backend.FoodHub.service;

import com.proyecto_backend.FoodHub.exception.UserAlreadyExistsException;
import com.proyecto_backend.FoodHub.exception.UserNotFoundException;
import com.proyecto_backend.FoodHub.model.Rol;
import com.proyecto_backend.FoodHub.model.Usuario;
import com.proyecto_backend.FoodHub.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Zahir");
        usuario.setCorreo("zahir@example.com");
        usuario.setContrasena("123456");
        usuario.setRol(Rol.ADMIN);
    }

    @Test
    @DisplayName("✅ listarTodos() debe devolver una lista de usuarios")
    void testListarTodos() {
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario));

        List<Usuario> resultado = usuarioService.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCorreo()).isEqualTo("zahir@example.com");
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("✅ buscarPorId() debe devolver el usuario si existe")
    void testBuscarPorId() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.buscarPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getCorreo()).isEqualTo("zahir@example.com");
        verify(usuarioRepository).findById(1L);
    }

    @Test
    @DisplayName("⚠️ buscarPorId() debe lanzar excepción si el usuario no existe")
    void testBuscarPorIdNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> usuarioService.buscarPorId(99L));
        verify(usuarioRepository).findById(99L);
    }

    @Test
    @DisplayName("✅ guardar() debe guardar un nuevo usuario si el correo no existe")
    void testGuardarUsuarioNuevo() {
        when(usuarioRepository.findByCorreo(usuario.getCorreo())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.guardar(usuario);

        assertThat(resultado.getCorreo()).isEqualTo("zahir@example.com");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("⚠️ guardar() debe lanzar excepción si el correo ya existe")
    void testGuardarUsuarioExistente() {
        when(usuarioRepository.findByCorreo(usuario.getCorreo())).thenReturn(Optional.of(usuario));

        assertThrows(UserAlreadyExistsException.class, () -> usuarioService.guardar(usuario));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("✅ eliminar() debe borrar usuario existente")
    void testEliminarUsuario() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        usuarioService.eliminar(1L);

        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    @DisplayName("⚠️ eliminar() debe lanzar excepción si el usuario no existe")
    void testEliminarUsuarioNoExiste() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> usuarioService.eliminar(99L));
        verify(usuarioRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("✅ buscarPorCorreo() debe devolver usuario si existe")
    void testBuscarPorCorreo() {
        when(usuarioRepository.findByCorreo("zahir@example.com")).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.buscarPorCorreo("zahir@example.com");

        assertThat(resultado.getNombre()).isEqualTo("Zahir");
        verify(usuarioRepository).findByCorreo("zahir@example.com");
    }

    @Test
    @DisplayName("⚠️ buscarPorCorreo() debe lanzar excepción si no existe")
    void testBuscarPorCorreoNoExiste() {
        when(usuarioRepository.findByCorreo("noexiste@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> usuarioService.buscarPorCorreo("noexiste@example.com"));
    }
}
