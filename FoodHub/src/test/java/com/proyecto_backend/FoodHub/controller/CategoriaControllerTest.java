package com.proyecto_backend.FoodHub.controller;

import com.proyecto_backend.FoodHub.dto.CategoriaDTO;
import com.proyecto_backend.FoodHub.service.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoriaControllerTest {

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private CategoriaController categoriaController;

    private CategoriaDTO categoria1;
    private CategoriaDTO categoria2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        categoria1 = new CategoriaDTO();
        categoria1.setId(1L);
        categoria1.setNombre("Bebidas");

        categoria2 = new CategoriaDTO();
        categoria2.setId(2L);
        categoria2.setNombre("Postres");
    }

    // ✅ Test listarTodas()
    @Test
    void testListarTodas() {
        List<CategoriaDTO> lista = Arrays.asList(categoria1, categoria2);
        when(categoriaService.listarTodas()).thenReturn(lista);

        ResponseEntity<List<CategoriaDTO>> response = categoriaController.listarTodas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(categoriaService, times(1)).listarTodas();
    }

    // ✅ Test obtenerPorId() - encontrado
    @Test
    void testObtenerPorId_Encontrado() {
        when(categoriaService.buscarPorId(1L)).thenReturn(Optional.of(categoria1));

        ResponseEntity<CategoriaDTO> response = categoriaController.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Bebidas", response.getBody().getNombre());
        verify(categoriaService, times(1)).buscarPorId(1L);
    }

    // ✅ Test obtenerPorId() - no encontrado
    @Test
    void testObtenerPorId_NoEncontrado() {
        when(categoriaService.buscarPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<CategoriaDTO> response = categoriaController.obtenerPorId(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(categoriaService, times(1)).buscarPorId(99L);
    }

    // ✅ Test crear()
    @Test
    void testCrear() {
        when(categoriaService.guardar(categoria1)).thenReturn(categoria1);

        ResponseEntity<CategoriaDTO> response = categoriaController.crear(categoria1);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Bebidas", response.getBody().getNombre());
        verify(categoriaService, times(1)).guardar(categoria1);
    }

    // ✅ Test actualizar()
    @Test
    void testActualizar() {
        when(categoriaService.guardar(categoria1)).thenReturn(categoria1);

        ResponseEntity<CategoriaDTO> response = categoriaController.actualizar(1L, categoria1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(categoriaService, times(1)).guardar(categoria1);
    }

    // ✅ Test eliminar()
    @Test
    void testEliminar() {
        doNothing().when(categoriaService).eliminar(1L);

        ResponseEntity<Void> response = categoriaController.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(categoriaService, times(1)).eliminar(1L);
    }
}
