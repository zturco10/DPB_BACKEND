package com.proyecto_backend.FoodHub.repository;

import com.proyecto_backend.FoodHub.model.Categoria;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoriaRepositoryTest {

    // 🐘 Contenedor PostgreSQL para pruebas
    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16.0-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    // 🔧 Configuración dinámica de conexión
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria categoria1;
    private Categoria categoria2;
    private Categoria categoria3;

    @BeforeEach
    void setUp() {
        categoriaRepository.deleteAll();

        categoria1 = new Categoria();
        categoria1.setNombre("Bebidas");

        categoria2 = new Categoria();
        categoria2.setNombre("Postres");

        categoria3 = new Categoria();
        categoria3.setNombre("Comidas Rápidas");

        categoriaRepository.saveAll(List.of(categoria1, categoria2, categoria3));
    }

    @Test
    @DisplayName("Debe guardar una categoría correctamente")
    void shouldSaveCategoriaSuccessfully() {
        Categoria nueva = new Categoria();
        nueva.setNombre("Snacks");

        Categoria guardada = categoriaRepository.save(nueva);

        assertThat(guardada).isNotNull();
        assertThat(guardada.getId()).isNotNull();
        assertThat(guardada.getNombre()).isEqualTo("Snacks");
    }

    @Test
    @DisplayName("Debe encontrar una categoría por su nombre")
    void shouldFindByNombre() {
        Categoria encontrada = categoriaRepository.findByNombre("Postres");

        assertThat(encontrada).isNotNull();
        assertThat(encontrada.getNombre()).isEqualTo("Postres");
    }

    @Test
    @DisplayName("Debe listar todas las categorías")
    void shouldListAllCategorias() {
        List<Categoria> categorias = categoriaRepository.findAll();

        assertThat(categorias).hasSize(3);
        assertThat(categorias)
                .extracting(Categoria::getNombre)
                .containsExactlyInAnyOrder("Bebidas", "Postres", "Comidas Rápidas");
    }

    @Test
    @DisplayName("Debe eliminar una categoría correctamente")
    void shouldDeleteCategoria() {
        Categoria categoria = categoriaRepository.findByNombre("Bebidas");
        categoriaRepository.deleteById(categoria.getId());

        Optional<Categoria> eliminada = categoriaRepository.findById(categoria.getId());
        assertThat(eliminada).isEmpty();
    }
}
