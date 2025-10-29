package com.proyecto_backend.FoodHub.controller;

import com.proyecto_backend.FoodHub.dto.MenuDTO;
import com.proyecto_backend.FoodHub.service.MenuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Importar
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menus")
@CrossOrigin(origins = "*")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    @PreAuthorize("permitAll()") // Cualquiera puede ver los menús
    public ResponseEntity<List<MenuDTO>> listarTodos() {
        List<MenuDTO> menus = menuService.listarTodos();
        return ResponseEntity.ok(menus);
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()") // Cualquiera puede ver
    public ResponseEntity<MenuDTO> obtenerPorId(@PathVariable Long id) {
        MenuDTO menu = menuService.buscarPorId(id);
        return ResponseEntity.ok(menu);
    }

    @GetMapping("/dia/{dia}")
    @PreAuthorize("permitAll()") // Cualquiera puede ver
    public ResponseEntity<MenuDTO> obtenerPorDia(@PathVariable String dia) {
        MenuDTO menu = menuService.buscarPorDia(dia);
        return ResponseEntity.ok(menu);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    // ✅ Devuelve 201 Created
    public ResponseEntity<MenuDTO> crear(@RequestBody MenuDTO menuDTO) {
        MenuDTO nuevo = menuService.guardar(menuDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo Admin puede actualizar menús
    public ResponseEntity<MenuDTO> actualizar(@PathVariable Long id, @RequestBody MenuDTO menuDTO) {
        menuDTO.setId(id); // Aseguramos el ID
        MenuDTO actualizado = menuService.guardar(menuDTO);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo Admin puede borrar menús
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        menuService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}