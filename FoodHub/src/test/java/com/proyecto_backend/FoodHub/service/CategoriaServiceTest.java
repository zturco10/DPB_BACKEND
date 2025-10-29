package com.proyecto_backend.FoodHub.service;

import com.proyecto_backend.FoodHub.dto.CategoriaDTO;
import com.proyecto_backend.FoodHub.model.Categoria;
import com.proyecto_backend.FoodHub.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListarTodas() {
        Categoria cat1 = new Categoria(1L, "Bebidas", null);
        Categoria cat2 = new Categoria(2L, "Snacks", null);
        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(cat1, cat2));

        List<CategoriaDTO> lista = categoriaService.listarTodas();
        assertThat(lista).hasSize(2);
        assertThat(lista.get(0).getNombre()).isEqualTo("Bebidas");
    }

    @Test
    void testBuscarPorId() {
        Categoria cat = new Categoria(1L, "Postres", null);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(cat));

        Optional<CategoriaDTO> dto = categoriaService.buscarPorId(1L);
        assertThat(dto).isPresent();
        assertThat(dto.get().getNombre()).isEqualTo("Postres");
    }

    @Test
    void testGuardarNuevaCategoria() {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setNombre("Ensaladas");

        Categoria cat = new Categoria();
        cat.setId(1L);
        cat.setNombre("Ensaladas");

        when(categoriaRepository.save(any(Categoria.class))).thenReturn(cat);

        CategoriaDTO guardado = categoriaService.guardar(dto);
        assertThat(guardado.getId()).isEqualTo(1L);
        assertThat(guardado.getNombre()).isEqualTo("Ensaladas");
    }

    @Test
    void testEliminarCategoriaExistente() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoriaRepository).deleteById(1L);

        categoriaService.eliminar(1L);

        verify(categoriaRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarCategoriaNoExistente() {
        when(categoriaRepository.existsById(99L)).thenReturn(false);

        try {
            categoriaService.eliminar(99L);
        } catch (RuntimeException e) {
            assertThat(e).hasMessageContaining("Categoría no encontrada");
        }

        verify(categoriaRepository, never()).deleteById(99L);
    }
}
