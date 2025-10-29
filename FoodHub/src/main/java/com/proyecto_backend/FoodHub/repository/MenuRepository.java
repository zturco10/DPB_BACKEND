package com.proyecto_backend.FoodHub.repository;

import com.proyecto_backend.FoodHub.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    Menu findByDia(String dia);
}
