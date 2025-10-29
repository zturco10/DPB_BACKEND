package com.proyecto_backend.FoodHub.service;

import com.proyecto_backend.FoodHub.dto.MenuDTO;
import com.proyecto_backend.FoodHub.exception.ResourceNotFoundException;
import com.proyecto_backend.FoodHub.model.Menu;
import com.proyecto_backend.FoodHub.model.Producto;
import com.proyecto_backend.FoodHub.model.TipoMenu;
import com.proyecto_backend.FoodHub.repository.MenuRepository;
import com.proyecto_backend.FoodHub.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final ProductoRepository productoRepository; // Necesario para la relación

    public MenuService(MenuRepository menuRepository, ProductoRepository productoRepository) {
        this.menuRepository = menuRepository;
        this.productoRepository = productoRepository;
    }

    public List<MenuDTO> listarTodos() {
        return menuRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public MenuDTO buscarPorId(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu no encontrado con ID: " + id));
        return convertirADTO(menu);
    }

    public MenuDTO buscarPorDia(String dia) {
        Menu menu = menuRepository.findByDia(dia);
        if (menu == null) {
            throw new ResourceNotFoundException("Menu no encontrado para el día: " + dia);
        }
        return convertirADTO(menu);
    }

    public MenuDTO guardar(MenuDTO dto) {
        Menu menu;
        if (dto.getId() != null) {
            menu = menuRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Menu no encontrado con ID: " + dto.getId()));
        } else {
            menu = new Menu();
        }

        menu.setDia(dto.getDia());
        menu.setPrecio(dto.getPrecio());
        // Convertir String (del DTO) al Enum (del Modelo)
        menu.setTipo(TipoMenu.valueOf(dto.getTipo().toUpperCase()));

        // IMPORTANTE: La asignación de productos a un menú se hace desde el Producto
        // (porque Producto es el dueño de la relación @ManyToOne).
        // Aquí solo guardamos los datos del Menú.

        Menu guardado = menuRepository.save(menu);
        return convertirADTO(guardado);
    }

    public void eliminar(Long id) {
        if (!menuRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Menu no existe con ID: " + id);
        }
        menuRepository.deleteById(id);
    }

    // --- Helper de Conversión ---
    private MenuDTO convertirADTO(Menu menu) {
        MenuDTO dto = new MenuDTO();
        dto.setId(menu.getId());
        dto.setDia(menu.getDia());
        dto.setPrecio(menu.getPrecio());
        dto.setTipo(menu.getTipo().name()); // Convertir Enum a String

        // Mapeamos la lista de productos a una lista de IDs
        if (menu.getProductos() != null) {
            dto.setProductoIds(menu.getProductos().stream()
                    .map(Producto::getId)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}