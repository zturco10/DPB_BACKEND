package com.proyecto_backend.FoodHub.repository;

import com.proyecto_backend.FoodHub.model.Producto;
import com.proyecto_backend.FoodHub.model.Resena;
import com.proyecto_backend.FoodHub.model.Usuario;
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
class ResenaRepositoryTest {

    // 🧩 Contenedor PostgreSQL temporal para las pruebas
    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("foodhub_test")
                    .withUsername("postgres")
                    .withPassword("postgres");

    // 🧩 Configurar conexión dinámica al contenedor
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private ResenaRepository resenaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    private Usuario usuario;
    private Producto producto;
    private Resena resena;

    @BeforeEach
    void setUp() {
        // Crear un usuario de prueba
        usuario = new Usuario();
        usuario.setNombre("Juan Pérez");
        usuario.setCorreo("juan@example.com");
        usuario.setContrasena("12345");
        usuarioRepository.save(usuario);

        // Crear un producto de prueba
        producto = new Producto();
        producto.setNombre("Hamburguesa");
        producto.setDescripcion("Hamburguesa clásica con queso");
        producto.setPrecio(15.0);
        productoRepository.save(producto);

        // Crear una reseña de prueba
        resena = new Resena();
        resena.setCalificacion(5);
        resena.setComentario("Excelente sabor y servicio");
        resena.setUsuario(usuario);
        resena.setProducto(producto);
        resenaRepository.save(resena);
    }

    @Test
    @DisplayName("Debe guardar una reseña correctamente")
    void testGuardarResena() {
        Resena nueva = new Resena();
        nueva.setCalificacion(4);
        nueva.setComentario("Muy bueno, aunque un poco caro");
        nueva.setUsuario(usuario);
        nueva.setProducto(producto);

        Resena guardada = resenaRepository.save(nueva);

        assertThat(guardada).isNotNull();
        assertThat(guardada.getId()).isNotNull();
        assertThat(guardada.getComentario()).contains("Muy bueno");
        assertThat(guardada.getCalificacion()).isEqualTo(4);
    }

    @Test
    @DisplayName("Debe encontrar reseñas por ID de producto")
    void testFindByProductoId() {
        List<Resena> lista = resenaRepository.findByProductoId(producto.getId());

        assertThat(lista).isNotEmpty();
        assertThat(lista.get(0).getProducto().getId()).isEqualTo(producto.getId());
    }

    @Test
    @DisplayName("Debe encontrar reseñas por ID de usuario")
    void testFindByUsuarioId() {
        List<Resena> lista = resenaRepository.findByUsuarioId(usuario.getId());

        assertThat(lista).isNotEmpty();
        assertThat(lista.get(0).getUsuario().getCorreo()).isEqualTo("juan@example.com");
    }

    @Test
    @DisplayName("Debe encontrar reseñas por calificación")
    void testFindByCalificacion() {
        List<Resena> lista = resenaRepository.findByCalificacion(5);

        assertThat(lista).isNotEmpty();
        assertThat(lista.get(0).getCalificacion()).isEqualTo(5);
    }

    @Test
    @DisplayName("Debe eliminar una reseña correctamente")
    void testEliminarResena() {
        resenaRepository.deleteById(resena.getId());
        boolean existe = resenaRepository.existsById(resena.getId());

        assertThat(existe).isFalse();
    }
}
