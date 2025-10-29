package com.proyecto_backend.FoodHub.repository;

import com.proyecto_backend.FoodHub.model.Kiosko;
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
class KioskoRepositoryTest {

    // 🧩 Contenedor PostgreSQL para pruebas
    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("foodhub_test")
                    .withUsername("postgres")
                    .withPassword("postgres");

    // 🧩 Configura las propiedades dinámicamente para Spring Boot
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private KioskoRepository kioskoRepository;

    private Kiosko kiosko;

    @BeforeEach
    void setUp() {
        kiosko = new Kiosko();
        kiosko.setNombre("Kiosko Central");
        kiosko.setUbicacion("Plaza Principal");
        kiosko.setHorario("8:00 - 18:00");
        kiosko.setDescripcion("Kiosko principal de la plaza"); // ✅ campo obligatorio
        kioskoRepository.save(kiosko);
    }

    @Test
    @DisplayName("Debe guardar un kiosko correctamente")
    void testGuardarKiosko() {
        Kiosko nuevo = new Kiosko();
        nuevo.setNombre("Kiosko Norte");
        nuevo.setUbicacion("Av. Los Olivos 123");
        nuevo.setHorario("9:00 - 19:00");
        nuevo.setDescripcion("Kiosko de la zona norte"); // ✅ requerido

        Kiosko guardado = kioskoRepository.save(nuevo);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getNombre()).isEqualTo("Kiosko Norte");
        assertThat(guardado.getDescripcion()).isEqualTo("Kiosko de la zona norte");
    }

    @Test
    @DisplayName("Debe encontrar un kiosko por su nombre")
    void testFindByNombre() {
        Kiosko encontrado = kioskoRepository.findByNombre("Kiosko Central");

        assertThat(encontrado).isNotNull();
        assertThat(encontrado.getUbicacion()).isEqualTo("Plaza Principal");
        assertThat(encontrado.getDescripcion()).isEqualTo("Kiosko principal de la plaza");
    }

    @Test
    @DisplayName("Debe listar todos los kioskos")
    void testListarKioskos() {
        List<Kiosko> kioskos = kioskoRepository.findAll();

        assertThat(kioskos).isNotEmpty();
        assertThat(kioskos.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("Debe eliminar un kiosko correctamente")
    void testEliminarKiosko() {
        kioskoRepository.deleteById(kiosko.getId());

        boolean existe = kioskoRepository.existsById(kiosko.getId());
        assertThat(existe).isFalse();
    }
}
