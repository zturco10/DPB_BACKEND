package com.proyecto_backend.FoodHub.controller;

import com.proyecto_backend.FoodHub.dto.ProductoDTO; // Para devolver el producto actualizado
import com.proyecto_backend.FoodHub.service.FileUploadService;
import com.proyecto_backend.FoodHub.service.ProductoService; // Necesitamos el servicio de producto
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/uploads") // Ruta base para subidas
@CrossOrigin(origins = "*")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private ProductoService productoService;

    @PostMapping("/productos/{id}/imagen")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<ProductoDTO> uploadProductoImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        // 1. Validar existencia y permiso (Busca el DTO COMPLETO)
        ProductoDTO productoExistente = productoService.buscarPorId(id);
        // productoService.validarPropietario(productoExistente, getUsuarioAutenticado()); // <-- ¡Ojo! Necesitarías ajustar validarPropietario para que acepte DTO o rehacer la validación aquí.

        // 2. Subir archivo
        String imageUrl = fileUploadService.uploadFile(file);

        // 3. Actualizar SOLO el campo imageUrl en el DTO existente
        productoExistente.setImageUrl(imageUrl);

        // 4. Guardar el DTO COMPLETO actualizado (con todos sus campos originales + la nueva URL)
        ProductoDTO productoActualizado = productoService.guardar(productoExistente);

        return ResponseEntity.ok(productoActualizado);
    }
}