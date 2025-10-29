package com.proyecto_backend.FoodHub.service;

import com.proyecto_backend.FoodHub.model.Categoria;
import com.proyecto_backend.FoodHub.dto.CategoriaDTO; // Importar DTO
import com.proyecto_backend.FoodHub.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Importar

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // 1. Modificado para devolver DTO
    public List<CategoriaDTO> listarTodas() {
        return categoriaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // 2. Modificado para devolver DTO
    public Optional<CategoriaDTO> buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .map(this::convertirADTO);
    }

    // 3. Modificado para recibir y devolver DTO
    public CategoriaDTO guardar(CategoriaDTO categoriaDTO) {
        Categoria categoria = new Categoria();
        // Si el DTO trae un ID, es una actualización
        if (categoriaDTO.getId() != null) {
            categoria = categoriaRepository.findById(categoriaDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        }
        categoria.setNombre(categoriaDTO.getNombre());

        Categoria guardada = categoriaRepository.save(categoria);
        return convertirADTO(guardada);
    }

    public void eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("Categoría no encontrada");
        }
        categoriaRepository.deleteById(id);
    }

    // --- Método Helper de Conversión ---

    private CategoriaDTO convertirADTO(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        return dto;
    }
}