package com.proyecto_backend.FoodHub.repository;

import com.proyecto_backend.FoodHub.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Producto findByNombre(String nombre);

    // ✅ MÉTODO NUEVO PARA BÚSQUEDA Y FILTRO
    @Query("SELECT p FROM Producto p WHERE " +
            "(:nombre IS NULL OR lower(p.nombre) LIKE lower(concat('%', :nombre, '%'))) AND " +
            "(:precioMax IS NULL OR p.precio <= :precioMax)")
    List<Producto> buscarProductos(
            @Param("nombre") String nombre,
            @Param("precioMax") Double precioMax
    );
}