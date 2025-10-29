package com.proyecto_backend.FoodHub.controller;

import com.proyecto_backend.FoodHub.dto.KioskoDTO; // Importar DTO
import com.proyecto_backend.FoodHub.service.KioskoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/kioskos")
@CrossOrigin(origins = "*")
public class KioskoController {

    private final KioskoService kioskoService;

    public KioskoController(KioskoService kioskoService) {
        this.kioskoService = kioskoService;
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<KioskoDTO>> listarTodos() {
        List<KioskoDTO> kioskos = kioskoService.listarTodos();
        return ResponseEntity.ok(kioskos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<KioskoDTO> obtenerPorId(@PathVariable Long id) {
        return kioskoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombre}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<KioskoDTO> obtenerPorNombre(@PathVariable String nombre) {
        KioskoDTO kiosko = kioskoService.buscarPorNombre(nombre);
        if (kiosko != null) {
            return ResponseEntity.ok(kiosko);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<KioskoDTO> crear(@RequestBody KioskoDTO kioskoDTO) {
        KioskoDTO nuevoKiosko = kioskoService.guardar(kioskoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoKiosko);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'KIOSKERO')")
    public ResponseEntity<KioskoDTO> actualizar(@PathVariable Long id, @RequestBody KioskoDTO kioskoDTO) {

        kioskoDTO.setId(id);
        KioskoDTO actualizado = kioskoService.guardar(kioskoDTO);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        kioskoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}