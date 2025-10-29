package com.proyecto_backend.FoodHub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto_backend.FoodHub.dto.UsuarioDTO;
import com.proyecto_backend.FoodHub.dto.UsuarioRegisterDTO;
import com.proyecto_backend.FoodHub.model.Rol;
import com.proyecto_backend.FoodHub.model.Usuario;
import com.proyecto_backend.FoodHub.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
    }

    @Test
    void testListarTodos() throws Exception {
        Usuario usuario1 = new Usuario(1L, "pass1", "Juan", "juan@mail.com", Rol.ADMIN, null, null);
        Usuario usuario2 = new Usuario(2L, "pass2", "Ana", "ana@mail.com", Rol.CLIENTE, null, null);
        List<Usuario> usuarios = Arrays.asList(usuario1, usuario2);

        when(usuarioService.listarTodos()).thenReturn(usuarios);

        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Juan"))
                .andExpect(jsonPath("$[1].correo").value("ana@mail.com"));

        verify(usuarioService, times(1)).listarTodos();
    }

    @Test
    void testBuscarPorId() throws Exception {
        Usuario usuario = new Usuario(1L, "pass1", "Juan", "juan@mail.com", Rol.ADMIN, null, null);
        when(usuarioService.buscarPorId(1L)).thenReturn(usuario);

        mockMvc.perform(get("/api/v1/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.correo").value("juan@mail.com"));

        verify(usuarioService, times(1)).buscarPorId(1L);
    }

    @Test
    void testBuscarPorCorreo() throws Exception {
        Usuario usuario = new Usuario(1L, "pass1", "Juan", "juan@mail.com", Rol.ADMIN, null, null);
        when(usuarioService.buscarPorCorreo("juan@mail.com")).thenReturn(usuario);

        mockMvc.perform(get("/api/v1/usuarios/correo/juan@mail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.rol").value("ADMIN"));

        verify(usuarioService, times(1)).buscarPorCorreo("juan@mail.com");
    }

    @Test
    void testCrear() throws Exception {
        UsuarioRegisterDTO dto = new UsuarioRegisterDTO();
        dto.setNombre("Juan");
        dto.setCorreo("juan@mail.com");
        dto.setContrasena("pass1");
        dto.setRol(Rol.ADMIN);

        Usuario guardado = new Usuario(1L, "pass1", "Juan", "juan@mail.com", Rol.ADMIN, null, null);
        when(usuarioService.guardar(any(Usuario.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan"));

        verify(usuarioService, times(1)).guardar(any(Usuario.class));
    }

    @Test
    void testActualizar() throws Exception {
        UsuarioRegisterDTO dto = new UsuarioRegisterDTO();
        dto.setNombre("Juan Actualizado");
        dto.setCorreo("juan@mail.com");
        dto.setContrasena("pass1");
        dto.setRol(Rol.ADMIN);

        Usuario existente = new Usuario(1L, "pass1", "Juan", "juan@mail.com", Rol.ADMIN, null, null);
        Usuario actualizado = new Usuario(1L, "pass1", "Juan Actualizado", "juan@mail.com", Rol.ADMIN, null, null);

        when(usuarioService.buscarPorId(1L)).thenReturn(existente);
        when(usuarioService.guardar(any(Usuario.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/v1/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Actualizado"));

        verify(usuarioService, times(1)).buscarPorId(1L);
        verify(usuarioService, times(1)).guardar(any(Usuario.class));
    }

    @Test
    void testEliminar() throws Exception {
        doNothing().when(usuarioService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/usuarios/1"))
                .andExpect(status().isNoContent());

        verify(usuarioService, times(1)).eliminar(1L);
    }
}
