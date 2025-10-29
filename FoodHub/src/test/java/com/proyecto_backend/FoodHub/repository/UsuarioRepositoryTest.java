package com.proyecto_backend.FoodHub.repository;

import com.proyecto_backend.FoodHub.model.Rol;
import com.proyecto_backend.FoodHub.model.Usuario;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers // ✅ Habilita Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // ✅ No reemplaza por H2
class UsuarioRepositoryTest {

    // ✅ Contenedor PostgreSQL (se inicia automáticamente antes de los tests)
    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("foodhub_test")
                    .withUsername("testuser")
                    .withPassword("testpass");

    // ✅ Conecta dinámicamente la app al contenedor
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("✅ Debería guardar un usuario correctamente")
    void testGuardarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Zahir");
        usuario.setCorreo("zahir@example.com");
        usuario.setContrasena("123456");
        usuario.setRol(Rol.ADMIN);

        Usuario guardado = usuarioRepository.save(usuario);

        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getCorreo()).isEqualTo("zahir@example.com");
    }

    @Test
    @DisplayName("✅ Debería encontrar un usuario por correo")
    void testFindByCorreo() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Jamil");
        usuario.setCorreo("jamil@example.com");
        usuario.setContrasena("abc123");
        usuario.setRol(Rol.CLIENTE);

        usuarioRepository.save(usuario);

        Optional<Usuario> resultado = usuarioRepository.findByCorreo("jamil@example.com");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Jamil");
    }

    @Test
    @DisplayName("⚠️ Debería devolver vacío si el correo no existe")
    void testFindByCorreoNoExiste() {
        Optional<Usuario> resultado = usuarioRepository.findByCorreo("inexistente@example.com");
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("🗑️ Debería eliminar un usuario correctamente")
    void testEliminarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Eliminar");
        usuario.setCorreo("eliminar@example.com");
        usuario.setContrasena("pass");
        usuario.setRol(Rol.KIOSKERO);

        Usuario guardado = usuarioRepository.save(usuario);
        Long id = guardado.getId();

        usuarioRepository.deleteById(id);

        Optional<Usuario> resultado = usuarioRepository.findById(id);
        assertThat(resultado).isEmpty();
    }
}
