package com.proyecto_backend.FoodHub.controller;

import com.proyecto_backend.FoodHub.dto.ResenaDTO;
import com.proyecto_backend.FoodHub.service.ResenaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ResenaControllerTest {

    @Mock
    private ResenaService resenaService;

    @InjectMocks
    private ResenaController resenaController;

    private ResenaDTO resena1;
    private ResenaDTO resena2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        resena1 = new ResenaDTO();
        resena1.setId(1L);
        resena1.setCalificacion(5);
        resena1.setComentario("Excelente producto");
        resena1.setUsuarioId(1L);
        resena1.setProductoId(1L);

        resena2 = new ResenaDTO();
        resena2.setId(2L);
        resena2.setCalificacion(3);
        resena2.setComentario("Regular");
        resena2.setUsuarioId(2L);
        resena2.setProductoId(1L);
    }

    @Test
    void testCrearResena() {
        when(resenaService.guardarResena(resena1)).thenReturn(resena1);

        ResponseEntity<ResenaDTO> response = resenaController.crearResena(resena1);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(resena1, response.getBody());
        verify(resenaService, times(1)).guardarResena(resena1);
    }

    @Test
    void testObtenerTodas() {
        when(resenaService.obtenerTodas()).thenReturn(Arrays.asList(resena1, resena2));

        ResponseEntity<List<ResenaDTO>> response = resenaController.obtenerTodas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(resenaService, times(1)).obtenerTodas();
    }

    @Test
    void testObtenerPorId() {
        when(resenaService.obtenerPorId(1L)).thenReturn(resena1);

        ResponseEntity<ResenaDTO> response = resenaController.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(resena1, response.getBody());
        verify(resenaService, times(1)).obtenerPorId(1L);
    }

    @Test
    void testObtenerPorProducto() {
        when(resenaService.obtenerPorProducto(1L)).thenReturn(Arrays.asList(resena1, resena2));

        ResponseEntity<List<ResenaDTO>> response = resenaController.obtenerPorProducto(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(resenaService, times(1)).obtenerPorProducto(1L);
    }

    @Test
    void testObtenerPorUsuario() {
        when(resenaService.obtenerPorUsuario(1L)).thenReturn(List.of(resena1));

        ResponseEntity<List<ResenaDTO>> response = resenaController.obtenerPorUsuario(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(resenaService, times(1)).obtenerPorUsuario(1L);
    }

    @Test
    void testObtenerPorCalificacion() {
        when(resenaService.obtenerPorCalificacion(5)).thenReturn(List.of(resena1));

        ResponseEntity<List<ResenaDTO>> response = resenaController.obtenerPorCalificacion(5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(resenaService, times(1)).obtenerPorCalificacion(5);
    }

    @Test
    void testActualizarResena() {
        ResenaDTO nuevaResena = new ResenaDTO();
        nuevaResena.setCalificacion(4);
        nuevaResena.setComentario("Mejorable");
        nuevaResena.setUsuarioId(1L);
        nuevaResena.setProductoId(1L);

        ResenaDTO resenaActualizada = new ResenaDTO();
        resenaActualizada.setId(1L);
        resenaActualizada.setCalificacion(4);
        resenaActualizada.setComentario("Mejorable");
        resenaActualizada.setUsuarioId(1L);
        resenaActualizada.setProductoId(1L);

        when(resenaService.guardarResena(nuevaResena)).thenReturn(resenaActualizada);

        ResponseEntity<ResenaDTO> response = resenaController.actualizarResena(1L, nuevaResena);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(resenaActualizada, response.getBody());
        verify(resenaService, times(1)).guardarResena(nuevaResena);
    }

    @Test
    void testEliminarResena() {
        doNothing().when(resenaService).eliminarResena(1L);

        ResponseEntity<Void> response = resenaController.eliminarResena(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(resenaService, times(1)).eliminarResena(1L);
    }
}
