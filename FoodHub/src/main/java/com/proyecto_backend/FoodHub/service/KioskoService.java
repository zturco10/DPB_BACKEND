package com.proyecto_backend.FoodHub.service;

import com.proyecto_backend.FoodHub.model.Kiosko;
import com.proyecto_backend.FoodHub.model.Producto;
import com.proyecto_backend.FoodHub.dto.KioskoDTO;
import com.proyecto_backend.FoodHub.repository.KioskoRepository;
import com.proyecto_backend.FoodHub.repository.ProductoRepository; // Importar
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KioskoService {

    private final KioskoRepository kioskoRepository;
    private final ProductoRepository productoRepository; // 1. Inyectar repo necesario

    public KioskoService(KioskoRepository kioskoRepository, ProductoRepository productoRepository) {
        this.kioskoRepository = kioskoRepository;
        this.productoRepository = productoRepository; // 2. Inicializar
    }

    public List<KioskoDTO> listarTodos() {
        return kioskoRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public Optional<KioskoDTO> buscarPorId(Long id) {
        return kioskoRepository.findById(id)
                .map(this::convertirADTO);
    }

    // 3. Lógica de creación/actualización con DTO
    public KioskoDTO guardar(KioskoDTO dto) {
        Kiosko kiosko = new Kiosko();

        if (dto.getId() != null) {
            kiosko = kioskoRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Kiosko no encontrado"));
        }

        kiosko.setNombre(dto.getNombre());
        kiosko.setUbicacion(dto.getUbicacion());
        kiosko.setHorario(dto.getHorario());
        // Asumo que Kiosko.java tiene descripcion
        // kiosko.setDescripcion(dto.getDescripcion());

        // 4. Lógica para manejar la relación N:M
        if (dto.getProductoIds() != null) {
            List<Producto> productos = productoRepository.findAllById(dto.getProductoIds());
            kiosko.setProductos(productos);
        }

        Kiosko guardado = kioskoRepository.save(kiosko);
        return convertirADTO(guardado);
    }

    public void eliminar(Long id) {
        if (!kioskoRepository.existsById(id)) {
            throw new RuntimeException("Kiosko no encontrado");
        }
        kioskoRepository.deleteById(id);
    }

    // (Opcional: si mantienes buscarPorNombre)
    public KioskoDTO buscarPorNombre(String nombre) {
        Kiosko kiosko = kioskoRepository.findByNombre(nombre);
        return (kiosko != null) ? convertirADTO(kiosko) : null;
    }

    // --- Método Helper de Conversión ---
    private KioskoDTO convertirADTO(Kiosko kiosko) {
        KioskoDTO dto = new KioskoDTO();
        dto.setId(kiosko.getId());
        dto.setNombre(kiosko.getNombre());
        dto.setUbicacion(kiosko.getUbicacion());
        dto.setHorario(kiosko.getHorario());

        // 5. Convertir la lista de Productos a lista de IDs
        if (kiosko.getProductos() != null) {
            List<Long> productoIds = kiosko.getProductos().stream()
                    .map(Producto::getId)
                    .collect(Collectors.toList());
            dto.setProductoIds(productoIds);
        }
        return dto;
    }
}