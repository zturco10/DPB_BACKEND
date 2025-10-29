package com.proyecto_backend.FoodHub.service;

import com.proyecto_backend.FoodHub.dto.MenuDTO;
import com.proyecto_backend.FoodHub.exception.ResourceNotFoundException;
import com.proyecto_backend.FoodHub.model.Menu;
import com.proyecto_backend.FoodHub.model.TipoMenu;
import com.proyecto_backend.FoodHub.repository.MenuRepository;
import com.proyecto_backend.FoodHub.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private MenuService menuService;

    private Menu menu1;
    private Menu menu2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        menu1 = new Menu();
        menu1.setId(1L);
        menu1.setDia("Lunes");
        menu1.setPrecio(25.0);
        menu1.setTipo(TipoMenu.ECONOMICO);

        menu2 = new Menu();
        menu2.setId(2L);
        menu2.setDia("Martes");
        menu2.setPrecio(30.0);
        menu2.setTipo(TipoMenu.ESTUDIANTIL);
    }

    @Test
    void listarTodos_DeberiaRetornarTodosLosMenus() {
        when(menuRepository.findAll()).thenReturn(Arrays.asList(menu1, menu2));

        List<MenuDTO> result = menuService.listarTodos();

        assertEquals(2, result.size());
        assertEquals("Lunes", result.get(0).getDia());
        assertEquals("Martes", result.get(1).getDia());
        verify(menuRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_Existente_DeberiaRetornarMenu() {
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu1));

        MenuDTO dto = menuService.buscarPorId(1L);

        assertEquals("Lunes", dto.getDia());
        assertEquals("ECONOMICO", dto.getTipo());
    }

    @Test
    void buscarPorId_NoExistente_DeberiaLanzarExcepcion() {
        when(menuRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> menuService.buscarPorId(99L));
    }

    @Test
    void buscarPorDia_Existente_DeberiaRetornarMenu() {
        when(menuRepository.findByDia("Lunes")).thenReturn(menu1);

        MenuDTO dto = menuService.buscarPorDia("Lunes");

        assertEquals(1L, dto.getId());
        assertEquals("ECONOMICO", dto.getTipo());
    }

    @Test
    void buscarPorDia_NoExistente_DeberiaLanzarExcepcion() {
        when(menuRepository.findByDia("Domingo")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> menuService.buscarPorDia("Domingo"));
    }

    @Test
    void guardar_NuevoMenu_DeberiaGuardarYRetornarDTO() {
        MenuDTO nuevoDTO = new MenuDTO();
        nuevoDTO.setDia("Miércoles");
        nuevoDTO.setPrecio(28.0);
        nuevoDTO.setTipo("EJECUTIVO");

        Menu menuGuardado = new Menu();
        menuGuardado.setId(3L);
        menuGuardado.setDia("Miércoles");
        menuGuardado.setPrecio(28.0);
        menuGuardado.setTipo(TipoMenu.EJECUTIVO);

        when(menuRepository.save(any(Menu.class))).thenReturn(menuGuardado);

        MenuDTO result = menuService.guardar(nuevoDTO);

        assertEquals(3L, result.getId());
        assertEquals("Miércoles", result.getDia());
        assertEquals("EJECUTIVO", result.getTipo());
    }

    @Test
    void eliminar_Existente_DeberiaLlamarDelete() {
        when(menuRepository.existsById(1L)).thenReturn(true);

        menuService.eliminar(1L);

        verify(menuRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_NoExistente_DeberiaLanzarExcepcion() {
        when(menuRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> menuService.eliminar(99L));
    }
}
