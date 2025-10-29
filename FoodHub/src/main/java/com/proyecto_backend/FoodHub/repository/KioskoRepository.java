package com.proyecto_backend.FoodHub.repository;

import com.proyecto_backend.FoodHub.model.Kiosko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KioskoRepository extends JpaRepository<Kiosko, Long> {

    Kiosko findByNombre(String nombre);
}
