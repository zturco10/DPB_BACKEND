package com.proyecto_backend.FoodHub.controller;

import com.proyecto_backend.FoodHub.dto.KioskoDTO;
import com.proyecto_backend.FoodHub.service.KioskoService;
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

public class KioskoControllerTest {

    @Mock
    private KioskoService kioskoService;

    @InjectMocks
    private KioskoController kioskoController;

    private KioskoDTO kiosko1;
    private KioskoDTO kiosko2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        kiosko1 = new KioskoDTO();
        kiosko1.setId(1L);
        kiosko1.setNombre("Kiosko Central");
        kiosko1.setUbicacion("Campus Norte");
        kiosko1.setHorario("8:00 - 18:00");

        kiosko2 = new KioskoDTO();
        kiosko2.setId(2L);
        kiosko2.setNombre("Kiosko Sur");
        kiosko2.setUbicacion("Campus Sur");
        kiosko2.setHorario("9:00 - 17:00");
    }

    // ✅ Test listarTodos()
    @Test
    void testListarTodos() {
        List<KioskoDTO> kioskos = Arrays.asList(kiosko1, kiosko2);
        when(kioskoService.listarTodos()).thenReturn(kioskos);

        ResponseEntity<List<KioskoDTO>> response = kioskoController.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(kioskoService, times(1)).listarTodos();
    }

    // ✅ Test obtenerPorId() - encontrado
    @Test
    void testObtenerPorId_Encontrado() {
        when(kioskoService.buscarPorId(1L)).thenReturn(Optional.of(kiosko1));

        ResponseEntity<KioskoDTO> response = kioskoController.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Kiosko Central", response.getBody().getNombre());
        verify(kioskoService, times(1)).buscarPorId(1L);
    }

    // ✅ Test obtenerPorId() - no encontrado
    @Test
    void testObtenerPorId_NoEncontrado() {
        when(kioskoService.buscarPorId(99L)).thenReturn(Optional.empty());

        ResponseEntity<KioskoDTO> response = kioskoController.obtenerPorId(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // ✅ Test obtenerPorNombre() - encontrado
    @Test
    void testObtenerPorNombre_Encontrado() {
        when(kioskoService.buscarPorNombre("Kiosko Central")).thenReturn(kiosko1);

        ResponseEntity<KioskoDTO> response = kioskoController.obtenerPorNombre("Kiosko Central");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Campus Norte", response.getBody().getUbicacion());
        verify(kioskoService, times(1)).buscarPorNombre("Kiosko Central");
    }

    // ✅ Test obtenerPorNombre() - no encontrado
    @Test
    void testObtenerPorNombre_NoEncontrado() {
        when(kioskoService.buscarPorNombre("Inexistente")).thenReturn(null);

        ResponseEntity<KioskoDTO> response = kioskoController.obtenerPorNombre("Inexistente");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // ✅ Test crear()
    @Test
    void testCrear() {
        when(kioskoService.guardar(kiosko1)).thenReturn(kiosko1);

        ResponseEntity<KioskoDTO> response = kioskoController.crear(kiosko1);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(kiosko1, response.getBody());
        verify(kioskoService, times(1)).guardar(kiosko1);
    }

    // ✅ Test actualizar()
    @Test
    void testActualizar() {
        when(kioskoService.guardar(kiosko1)).thenReturn(kiosko1);

        ResponseEntity<KioskoDTO> response = kioskoController.actualizar(1L, kiosko1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(kioskoService, times(1)).guardar(kiosko1);
    }

    // ✅ Test eliminar()
    @Test
    void testEliminar() {
        doNothing().when(kioskoService).eliminar(1L);

        ResponseEntity<Void> response = kioskoController.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(kioskoService, times(1)).eliminar(1L);
    }
}
