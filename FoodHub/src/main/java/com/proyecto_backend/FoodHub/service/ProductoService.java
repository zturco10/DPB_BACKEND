package com.proyecto_backend.FoodHub.service;

import com.proyecto_backend.FoodHub.dto.ProductoDTO;
import com.proyecto_backend.FoodHub.exception.AccesoNoAutorizadoException;
import com.proyecto_backend.FoodHub.exception.ResourceNotFoundException;
import com.proyecto_backend.FoodHub.model.*;
import com.proyecto_backend.FoodHub.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.proyecto_backend.FoodHub.event.NuevoProductoEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final MenuRepository menuRepository;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    // KioskoRepository no es necesario aquí para guardar,
    // porque Kiosko es el dueño de la relación N:M.

    public ProductoService(ProductoRepository productoRepository, UsuarioRepository usuarioRepository, CategoriaRepository categoriaRepository, MenuRepository menuRepository) {
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.menuRepository = menuRepository;
    }

    public List<ProductoDTO> listarTodos() {
        return productoRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public ProductoDTO buscarPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return convertirADTO(producto);
    }

    @Transactional
    public ProductoDTO guardar(ProductoDTO dto) {
        Producto producto;
        boolean isNewProduct = false;

        Usuario usuarioAutenticado = getUsuarioAutenticado();
        if (dto.getId() != null) {
            producto = productoRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + dto.getId()));
            validarPropietario(producto, usuarioAutenticado);
        } else {
            producto = new Producto();
            isNewProduct = true;
        }

        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        producto.setDescripcion(dto.getDescripcion());

        // Asignar Vendedor (Usuario)
        if (dto.getVendedorId() != null) {
            Usuario vendedor = usuarioRepository.findById(dto.getVendedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado con ID: " + dto.getVendedorId()));
            producto.setVendedor(vendedor);
        }

        // Asignar Categorías (N:M)
        if (dto.getCategoriaIds() != null && !dto.getCategoriaIds().isEmpty()) {
            List<Categoria> categorias = categoriaRepository.findAllById(dto.getCategoriaIds());
            producto.setCategorias(categorias);
        } else {
            producto.setCategorias(Collections.emptyList());
        }

        // Asignar a un Menú (Opcional)
        if (dto.getMenuId() != null) {
            Menu menu = menuRepository.findById(dto.getMenuId())
                    .orElseThrow(() -> new ResourceNotFoundException("Menú no encontrado con ID: " + dto.getMenuId()));
            producto.setMenu(menu);
        } else {
            producto.setMenu(null);
        }

        // La relación con Kiosko se maneja desde KioskoService, no aquí.

        Producto guardado = productoRepository.save(producto);

        if (isNewProduct) {
            NuevoProductoEvent evento = new NuevoProductoEvent(this, guardado.getId(), guardado.getNombre());
            eventPublisher.publishEvent(evento);
        }
        return convertirADTO(guardado);
    }

    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar. Producto no existe con ID: " + id));

        Usuario usuarioAutenticado = getUsuarioAutenticado();
        validarPropietario(producto, usuarioAutenticado);

        productoRepository.deleteById(id);
    }
    public List<ProductoDTO> buscarYFiltrar(String nombre, Double precioMax) {
        // El query JPQL maneja si los parámetros son null
        List<Producto> productos = productoRepository.buscarProductos(nombre, precioMax);

        return productos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private Usuario getUsuarioAutenticado() {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado en contexto de seguridad"));
    }

    /**
     * Valida si el usuario es el propietario del producto o es un ADMIN.
     */
    private void validarPropietario(Producto producto, Usuario usuario) {
        // Si el rol es ADMIN, tiene permiso y salimos
        if (usuario.getRol() == Rol.ADMIN) {
            return;
        }

        // Si no es ADMIN, verificamos si es el propietario
        if (producto.getVendedor() == null || !producto.getVendedor().getId().equals(usuario.getId())) {
            throw new AccesoNoAutorizadoException("No tienes permiso para modificar este producto.");
        }
    }

    public ProductoDTO buscarPorNombre(String nombre) {
        Producto producto = productoRepository.findByNombre(nombre);
        if (producto == null) {
            throw new ResourceNotFoundException("Producto no encontrado con nombre: " + nombre);
        }
        return convertirADTO(producto);
    }

    // --- Helper de Conversión ---
    private ProductoDTO convertirADTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setPrecio(producto.getPrecio());
        dto.setDescripcion(producto.getDescripcion());
        dto.setImageUrl(producto.getImageUrl());

        if (producto.getVendedor() != null) {
            dto.setVendedorId(producto.getVendedor().getId());
        }
        if (producto.getMenu() != null) {
            dto.setMenuId(producto.getMenu().getId());
        }
        if (producto.getCategorias() != null) {
            dto.setCategoriaIds(producto.getCategorias().stream()
                    .map(Categoria::getId)
                    .collect(Collectors.toList()));
        }
        if (producto.getKioskos() != null) {
            dto.setKioskoIds(producto.getKioskos().stream()
                    .map(Kiosko::getId)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}