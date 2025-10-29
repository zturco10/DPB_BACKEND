package com.proyecto_backend.FoodHub.repository;

import com.proyecto_backend.FoodHub.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Categoria findByNombre(String nombre);
}
