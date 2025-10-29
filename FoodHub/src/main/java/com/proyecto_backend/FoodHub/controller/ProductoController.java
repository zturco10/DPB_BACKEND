package com.proyecto_backend.FoodHub.controller;

import com.proyecto_backend.FoodHub.dto.ProductoDTO;
import com.proyecto_backend.FoodHub.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @PreAuthorize("permitAll()") // Cualquiera puede ver productos
    public ResponseEntity<List<ProductoDTO>> listarTodos() {
        List<ProductoDTO> productos = productoService.listarTodos();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()") // Cualquiera puede ver
    public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable Long id) {
        ProductoDTO producto = productoService.buscarPorId(id);
        return ResponseEntity.ok(producto);
    }

    @GetMapping("/nombre/{nombre}")
    @PreAuthorize("permitAll()") // Cualquiera puede ver
    public ResponseEntity<ProductoDTO> obtenerPorNombre(@PathVariable String nombre) {
        ProductoDTO producto = productoService.buscarPorNombre(nombre);
        return ResponseEntity.ok(producto);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    // ✅ Devuelve 201 Created
    public ResponseEntity<ProductoDTO> crearProducto(@RequestBody ProductoDTO productoDTO) {
        ProductoDTO nuevoProducto = productoService.guardar(productoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    @GetMapping("/buscar")
    @PreAuthorize("permitAll()") // Cualquiera puede buscar
    public ResponseEntity<List<ProductoDTO>> buscarProductos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Double precioMax) {

        List<ProductoDTO> productos = productoService.buscarYFiltrar(nombre, precioMax);
        return ResponseEntity.ok(productos);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'KIOSKERO', 'ADMIN')") // Clientes (dueños), Kioskeros y Admin pueden editar
    public ResponseEntity<ProductoDTO> actualizarProducto(@PathVariable Long id, @RequestBody ProductoDTO productoDTO) {
        // (Lógica avanzada faltante: verificar que el CLIENTE sea dueño del producto)
        productoDTO.setId(id);
        ProductoDTO actualizado = productoService.guardar(productoDTO);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')") // Clientes (dueños) y Admin pueden borrar
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        // (Lógica avanzada faltante: verificar que el CLIENTE sea dueño del producto)
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}