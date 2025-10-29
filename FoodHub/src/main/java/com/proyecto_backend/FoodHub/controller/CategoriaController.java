package com.proyecto_backend.FoodHub.controller;

import com.proyecto_backend.FoodHub.dto.CategoriaDTO; // Importar DTO
import com.proyecto_backend.FoodHub.service.CategoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listarTodas() {
        List<CategoriaDTO> categorias = categoriaService.listarTodas();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> obtenerPorId(@PathVariable Long id) {
        return categoriaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // No necesitas buscar por nombre si ya tienes el DTO,
    // pero si lo mantienes, debe devolver CategoriaDTO.

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    // ✅ Devuelve 201 Created
    public ResponseEntity<CategoriaDTO> crear(@RequestBody CategoriaDTO categoriaDTO) {
        CategoriaDTO nueva = categoriaService.guardar(categoriaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> actualizar(@PathVariable Long id, @RequestBody CategoriaDTO categoriaDTO) {
        categoriaDTO.setId(id); // Aseguramos que el ID esté en el DTO
        CategoriaDTO actualizado = categoriaService.guardar(categoriaDTO);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}