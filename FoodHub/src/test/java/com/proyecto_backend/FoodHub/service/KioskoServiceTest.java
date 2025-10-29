package com.proyecto_backend.FoodHub.service;

import com.proyecto_backend.FoodHub.dto.KioskoDTO;
import com.proyecto_backend.FoodHub.model.Kiosko;
import com.proyecto_backend.FoodHub.model.Producto;
import com.proyecto_backend.FoodHub.repository.KioskoRepository;
import com.proyecto_backend.FoodHub.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KioskoServiceTest {

    @Mock
    private KioskoRepository kioskoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private KioskoService kioskoService;

    private Kiosko kiosko1;
    private Kiosko kiosko2;
    private Producto producto1;
    private Producto producto2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        producto1 = new Producto();
        producto1.setId(1L);
        producto2 = new Producto();
        producto2.setId(2L);

        kiosko1 = new Kiosko(1L, "Kiosko A", "Ubicacion A", "08:00-20:00", "Descripcion A", Arrays.asList(producto1, producto2));
        kiosko2 = new Kiosko(2L, "Kiosko B", "Ubicacion B", "09:00-21:00", "Descripcion B", Arrays.asList(producto2));
    }

    @Test
    void listarTodos_debeRetornarTodosLosKioskos() {
        when(kioskoRepository.findAll()).thenReturn(Arrays.asList(kiosko1, kiosko2));

        List<KioskoDTO> result = kioskoService.listarTodos();

        assertEquals(2, result.size());
        assertEquals("Kiosko A", result.get(0).getNombre());
        assertEquals("Kiosko B", result.get(1).getNombre());
        verify(kioskoRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_existente_debeRetornarKiosko() {
        when(kioskoRepository.findById(1L)).thenReturn(Optional.of(kiosko1));

        Optional<KioskoDTO> result = kioskoService.buscarPorId(1L);

        assertTrue(result.isPresent());
        assertEquals("Kiosko A", result.get().getNombre());
    }

    @Test
    void guardar_nuevoKiosko_debeGuardarYRetornarDTO() {
        KioskoDTO dto = new KioskoDTO();
        dto.setNombre("Nuevo Kiosko");
        dto.setUbicacion("Ubicacion N");
        dto.setHorario("10:00-22:00");
        dto.setProductoIds(Arrays.asList(1L, 2L));

        when(productoRepository.findAllById(dto.getProductoIds())).thenReturn(Arrays.asList(producto1, producto2));
        when(kioskoRepository.save(any(Kiosko.class))).thenAnswer(i -> i.getArgument(0));

        KioskoDTO saved = kioskoService.guardar(dto);

        assertEquals("Nuevo Kiosko", saved.getNombre());
        assertEquals(2, saved.getProductoIds().size());

        ArgumentCaptor<Kiosko> captor = ArgumentCaptor.forClass(Kiosko.class);
        verify(kioskoRepository).save(captor.capture());
        assertEquals("Nuevo Kiosko", captor.getValue().getNombre());
    }

    @Test
    void eliminar_existente_debeEliminar() {
        when(kioskoRepository.existsById(1L)).thenReturn(true);

        kioskoService.eliminar(1L);

        verify(kioskoRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_noExistente_debeLanzarException() {
        when(kioskoRepository.existsById(1L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> kioskoService.eliminar(1L));
        assertEquals("Kiosko no encontrado", exception.getMessage());
    }

    @Test
    void buscarPorNombre_existente_debeRetornarKioskoDTO() {
        when(kioskoRepository.findByNombre("Kiosko A")).thenReturn(kiosko1);

        KioskoDTO result = kioskoService.buscarPorNombre("Kiosko A");

        assertNotNull(result);
        assertEquals("Kiosko A", result.getNombre());
    }

    @Test
    void buscarPorNombre_noExistente_debeRetornarNull() {
        when(kioskoRepository.findByNombre("Kiosko X")).thenReturn(null);

        KioskoDTO result = kioskoService.buscarPorNombre("Kiosko X");

        assertNull(result);
    }
}
