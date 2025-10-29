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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResenaServiceTest {

    @InjectMocks
    private ResenaService resenaService;

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private Usuario usuario;
    private Producto producto;
    private Resena resena;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setRol(Rol.CLIENTE);
        usuario.setCorreo("test@correo.com");

        producto = new Producto();
        producto.setId(1L);

        resena = new Resena(1L, 5, "Excelente", usuario, producto);

        // Mock de SecurityContext
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("test@correo.com");
        when(usuarioRepository.findByCorreo("test@correo.com")).thenReturn(Optional.of(usuario));
    }

    @Test
    void guardarResena_nuevaResena_exito() {
        ResenaDTO dto = new ResenaDTO();
        dto.setCalificacion(5);
        dto.setComentario("Excelente");
        dto.setUsuarioId(1L);
        dto.setProductoId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(resenaRepository.save(any(Resena.class))).thenAnswer(invocation -> {
            Resena r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });

        ResenaDTO resultado = resenaService.guardarResena(dto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Excelente", resultado.getComentario());
        verify(resenaRepository, times(1)).save(any(Resena.class));
    }

    @Test
    void obtenerTodas_exito() {
        when(resenaRepository.findAll()).thenReturn(List.of(resena));

        List<ResenaDTO> resultado = resenaService.obtenerTodas();

        assertEquals(1, resultado.size());
        assertEquals("Excelente", resultado.get(0).getComentario());
    }

    @Test
    void obtenerPorId_existente_exito() {
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));

        ResenaDTO dto = resenaService.obtenerPorId(1L);

        assertEquals(1L, dto.getId());
        assertEquals(5, dto.getCalificacion());
    }

    @Test
    void obtenerPorId_noExistente_lanzaExcepcion() {
        when(resenaRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> resenaService.obtenerPorId(2L));
    }

    @Test
    void eliminarResena_propietarioExito() {
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));

        resenaService.eliminarResena(1L);

        verify(resenaRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarResena_noPropietario_lanzaExcepcion() {
        Usuario otroUsuario = new Usuario();
        otroUsuario.setId(2L);
        otroUsuario.setRol(Rol.CLIENTE);
        resena.setUsuario(otroUsuario);

        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));

        assertThrows(AccesoNoAutorizadoException.class, () -> resenaService.eliminarResena(1L));
    }

    @Test
    void obtenerPorProducto_existente_exito() {
        when(productoRepository.existsById(1L)).thenReturn(true);
        when(resenaRepository.findByProductoId(1L)).thenReturn(List.of(resena));

        List<ResenaDTO> resultado = resenaService.obtenerPorProducto(1L);

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPorProducto_noExistente_lanzaExcepcion() {
        when(productoRepository.existsById(2L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> resenaService.obtenerPorProducto(2L));
    }

    @Test
    void obtenerPorUsuario_existente_exito() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(resenaRepository.findByUsuarioId(1L)).thenReturn(List.of(resena));

        List<ResenaDTO> resultado = resenaService.obtenerPorUsuario(1L);

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPorUsuario_noExistente_lanzaExcepcion() {
        when(usuarioRepository.existsById(2L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> resenaService.obtenerPorUsuario(2L));
    }

    @Test
    void obtenerPorCalificacion_exito() {
        when(resenaRepository.findByCalificacion(5)).thenReturn(List.of(resena));

        List<ResenaDTO> resultado = resenaService.obtenerPorCalificacion(5);

        assertEquals(1, resultado.size());
        assertEquals(5, resultado.get(0).getCalificacion());
    }
}
