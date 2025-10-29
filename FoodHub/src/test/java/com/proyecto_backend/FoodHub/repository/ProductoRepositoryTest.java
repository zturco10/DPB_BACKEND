
package com.proyecto_backend.FoodHub.repository;

import com.proyecto_backend.FoodHub.model.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
// ✅ 1. Imports necesarios para TestContainers
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
@Testcontainers // ✅ 2. Le dice a JUnit que active TestContainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // ✅ 3. Desactiva la BD en memoria (H2)
public class ProductoRepositoryTest {

    // ✅ 4. Define el contenedor de Docker que se creará
    @Container
    static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:16.0-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    // ✅ 5. Conecta dinámicamente Spring a la BD que se acaba de crear en Docker
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
        // Opcional: le decimos a Hibernate que use el dialecto de Postgres
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @Autowired
    private ProductoRepository productoRepository;

    private Producto producto1;
    private Producto producto2;
    private Producto producto3;

    @BeforeEach
    void setUp() {
        productoRepository.deleteAll();

        // ✅ Usar constructor vacío y setters
        producto1 = new Producto();
        producto1.setNombre("Pollo a la Brasa");
        producto1.setPrecio(15.0);
        producto1.setDescripcion("Pollo jugoso");
        // No necesitamos setear relaciones (kioskos, vendedor, etc.) ni imageUrl para este test

        producto2 =  new Producto();
        producto2.setNombre("Lomo Saltado");
        producto2.setPrecio(20.0);
        producto2.setDescripcion("Carne de res");

        producto3 = new Producto();
        producto3.setNombre("Gaseosa Inka");
        producto3.setPrecio(5.0);
        producto3.setDescripcion("Bebida personal");

        productoRepository.saveAll(List.of(producto1, producto2, producto3));
    }




    @Test
    void shouldFindByNameWhenSearchingByNombre() {
        List<Producto> productos = productoRepository.buscarProductos("pollo", null);

        assertThat(productos).isNotNull();
        assertThat(productos).hasSize(1);
        assertThat(productos.get(0).getNombre()).isEqualTo("Pollo a la Brasa");
    }


    @Test
    void shouldFindProductsWhenSearchingByPrecioMax() {
        List<Producto> productos = productoRepository.buscarProductos(null, 18.0);

        assertThat(productos).isNotNull();
        assertThat(productos).hasSize(2);
        assertThat(productos).extracting(Producto::getNombre)
                .containsExactlyInAnyOrder("Pollo a la Brasa", "Gaseosa Inka");
    }


    @Test
    void shouldFindProductsWhenSearchingByNombreAndPrecioMax() {
        List<Producto> productos = productoRepository.buscarProductos("o", 10.0);

        assertThat(productos).isNotNull();
        assertThat(productos).hasSize(1);
        assertThat(productos.get(0).getNombre()).isEqualTo("Gaseosa Inka");
    }


    @Test
    void shouldReturnEmptyListWhenNoMatches() {
        List<Producto> productos = productoRepository.buscarProductos("Ceviche", 50.0);

        assertThat(productos).isNotNull();
        assertThat(productos).isEmpty();
    }
}
