package com.proyecto_backend.FoodHub.controller;

import com.proyecto_backend.FoodHub.dto.ResenaDTO;
import com.proyecto_backend.FoodHub.service.ResenaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resenas")
@CrossOrigin(origins = "*")
public class ResenaController {

    @Autowired
    private ResenaService resenaService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    // ✅ Devuelve 201 Created
    public ResponseEntity<ResenaDTO> crearResena(@RequestBody ResenaDTO resenaDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resenaService.guardarResena(resenaDTO));
    }

    @GetMapping
    @PreAuthorize("permitAll()") // Cualquiera puede leer
    public ResponseEntity<List<ResenaDTO>> obtenerTodas() {
        return ResponseEntity.ok(resenaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()") // Cualquiera puede leer
    public ResponseEntity<ResenaDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(resenaService.obtenerPorId(id));
    }

    @GetMapping("/producto/{productoId}")
    @PreAuthorize("permitAll()") // Cualquiera puede leer
    public ResponseEntity<List<ResenaDTO>> obtenerPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(resenaService.obtenerPorProducto(productoId));
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("permitAll()") // Cualquiera puede leer
    public ResponseEntity<List<ResenaDTO>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(resenaService.obtenerPorUsuario(usuarioId));
    }

    @GetMapping("/calificacion/{valor}")
    @PreAuthorize("permitAll()") // Cualquiera puede leer
    public ResponseEntity<List<ResenaDTO>> obtenerPorCalificacion(@PathVariable int valor) {
        return ResponseEntity.ok(resenaService.obtenerPorCalificacion(valor));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')") // Solo el Cliente dueño puede actualizar
    public ResponseEntity<ResenaDTO> actualizarResena(@PathVariable Long id, @RequestBody ResenaDTO nuevaResenaDTO) {
        // (Lógica avanzada faltante: verificar que el usuario autenticado sea el dueño de la reseña)
        nuevaResenaDTO.setId(id);
        return ResponseEntity.ok(resenaService.guardarResena(nuevaResenaDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')") // El dueño o un Admin pueden borrar
    public ResponseEntity<Void> eliminarResena(@PathVariable Long id) {
        // (Lógica avanzada faltante: verificar que el usuario autenticado sea el dueño de la reseña)
        resenaService.eliminarResena(id);
        return ResponseEntity.noContent().build();
    }
}