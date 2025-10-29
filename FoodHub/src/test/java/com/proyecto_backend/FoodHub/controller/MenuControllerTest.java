package com.proyecto_backend.FoodHub.controller;

import com.proyecto_backend.FoodHub.dto.MenuDTO;
import com.proyecto_backend.FoodHub.service.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MenuControllerTest {

    @Mock
    private MenuService menuService;

    @InjectMocks
    private MenuController menuController;

    private MenuDTO menu1;
    private MenuDTO menu2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        menu1 = new MenuDTO();
        menu1.setId(1L);
        menu1.setTipo("ECONOMICO");
        menu1.setDia("Lunes");
        menu1.setPrecio(10.5);

        menu2 = new MenuDTO();
        menu2.setId(2L);
        menu2.setTipo("EJECUTIVO");
        menu2.setDia("Martes");
        menu2.setPrecio(15.0);
    }

    // ✅ Test listarTodos()
    @Test
    void testListarTodos() {
        List<MenuDTO> lista = Arrays.asList(menu1, menu2);
        when(menuService.listarTodos()).thenReturn(lista);

        ResponseEntity<List<MenuDTO>> response = menuController.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(menuService, times(1)).listarTodos();
    }

    // ✅ Test obtenerPorId()
    @Test
    void testObtenerPorId() {
        when(menuService.buscarPorId(1L)).thenReturn(menu1);

        ResponseEntity<MenuDTO> response = menuController.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Lunes", response.getBody().getDia());
        verify(menuService, times(1)).buscarPorId(1L);
    }

    // ✅ Test obtenerPorDia()
    @Test
    void testObtenerPorDia() {
        when(menuService.buscarPorDia("Martes")).thenReturn(menu2);

        ResponseEntity<MenuDTO> response = menuController.obtenerPorDia("Martes");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("EJECUTIVO", response.getBody().getTipo());
        verify(menuService, times(1)).buscarPorDia("Martes");
    }

    // ✅ Test crear()
    @Test
    void testCrear() {
        when(menuService.guardar(menu1)).thenReturn(menu1);

        ResponseEntity<MenuDTO> response = menuController.crear(menu1);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(menu1, response.getBody());
        verify(menuService, times(1)).guardar(menu1);
    }

    // ✅ Test actualizar()
    @Test
    void testActualizar() {
        when(menuService.guardar(menu1)).thenReturn(menu1);

        ResponseEntity<MenuDTO> response = menuController.actualizar(1L, menu1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(menuService, times(1)).guardar(menu1);
    }

    // ✅ Test eliminar()
    @Test
    void testEliminar() {
        doNothing().when(menuService).eliminar(1L);

        ResponseEntity<Void> response = menuController.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(menuService, times(1)).eliminar(1L);
    }
}
