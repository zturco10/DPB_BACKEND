package com.proyecto_backend.FoodHub.repository;

import com.proyecto_backend.FoodHub.model.Menu;
import com.proyecto_backend.FoodHub.model.TipoMenu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MenuRepositoryTest {

    // 🧩 Contenedor PostgreSQL para pruebas
    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("foodhub_test")
                    .withUsername("postgres")
                    .withPassword("postgres");

    // 🧩 Configuración dinámica del datasource
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private MenuRepository menuRepository;

    private Menu menu;

    @BeforeEach
    void setUp() {
        menu = new Menu();
        menu.setTipo(TipoMenu.ECONOMICO);
        menu.setDia("Lunes");
        menu.setPrecio(12.50);
        menuRepository.save(menu);
    }

    @Test
    @DisplayName("Debe guardar un menú correctamente")
    void testGuardarMenu() {
        Menu nuevo = new Menu();
        nuevo.setTipo(TipoMenu.SALUDABLE);
        nuevo.setDia("Martes");
        nuevo.setPrecio(18.0);

        Menu guardado = menuRepository.save(nuevo);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getTipo()).isEqualTo(TipoMenu.SALUDABLE);
        assertThat(guardado.getDia()).isEqualTo("Martes");
        assertThat(guardado.getPrecio()).isEqualTo(18.0);
    }

    @Test
    @DisplayName("Debe encontrar un menú por su día")
    void testFindByDia() {
        Menu encontrado = menuRepository.findByDia("Lunes");

        assertThat(encontrado).isNotNull();
        assertThat(encontrado.getTipo()).isEqualTo(TipoMenu.ECONOMICO);
        assertThat(encontrado.getPrecio()).isEqualTo(12.50);
    }

    @Test
    @DisplayName("Debe listar todos los menús")
    void testListarMenus() {
        List<Menu> menus = menuRepository.findAll();

        assertThat(menus).isNotEmpty();
        assertThat(menus.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("Debe eliminar un menú correctamente")
    void testEliminarMenu() {
        menuRepository.deleteById(menu.getId());

        boolean existe = menuRepository.existsById(menu.getId());
        assertThat(existe).isFalse();
    }
}
