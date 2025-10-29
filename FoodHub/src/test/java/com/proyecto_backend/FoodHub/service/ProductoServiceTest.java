package com.proyecto_backend.FoodHub.service;

import com.proyecto_backend.FoodHub.dto.ProductoDTO;
import com.proyecto_backend.FoodHub.exception.AccesoNoAutorizadoException;
import com.proyecto_backend.FoodHub.exception.ResourceNotFoundException;
import com.proyecto_backend.FoodHub.model.Producto;
import com.proyecto_backend.FoodHub.model.Rol;
import com.proyecto_backend.FoodHub.model.Usuario;
import com.proyecto_backend.FoodHub.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private MenuRepository menuRepository;

    // Los mocks de seguridad se quedan declarados aquí
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private ProductoService productoService;

    private Producto productoExistente;
    private ProductoDTO productoDTO;
    private Usuario usuarioCliente;

    @BeforeEach
    void setUp() {
        // ⛔️ QUITAMOS la configuración de SecurityContextHolder de aquí ⛔️

        // Configuramos datos de prueba
        productoExistente = new Producto();
        productoExistente.setId(1L);
        productoExistente.setNombre("Lomo Saltado");
        // ... (resto de datos de productoExistente)

        usuarioCliente = new Usuario();
        usuarioCliente.setId(10L);
        usuarioCliente.setCorreo("cliente@test.com");
        usuarioCliente.setRol(Rol.CLIENTE);
        productoExistente.setVendedor(usuarioCliente);

        productoDTO = new ProductoDTO();
        productoDTO.setNombre("Lomo Saltado DTO");
        productoDTO.setPrecio(25.0);
        productoDTO.setDescripcion("DTO");
        productoDTO.setVendedorId(usuarioCliente.getId());
    }

    // --- Tests buscarPorId (Estos no necesitan seguridad, no cambian) ---
    @Test
    void shouldReturnProductoDTOWhenIdExists() {
        given(productoRepository.findById(anyLong())).willReturn(Optional.of(productoExistente));
        ProductoDTO encontradoDTO = productoService.buscarPorId(1L);
        assertThat(encontradoDTO).isNotNull();
        assertThat(encontradoDTO.getId()).isEqualTo(productoExistente.getId());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        given(productoRepository.findById(anyLong())).willReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {
            productoService.buscarPorId(99L);
        });
        verify(productoRepository, never()).save(any(Producto.class));
    }

    // --- Tests guardar (Creación) ---

    @Test
    void shouldSaveAndReturnProductoDTOWhenCreatingNewProducto() {
        // Given (Simulamos TODO lo necesario para ESTE test)

        // ✅ 1. Configuramos SecurityContext SOLO para este test
        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn(usuarioCliente.getCorreo());
        given(usuarioRepository.findByCorreo(usuarioCliente.getCorreo())).willReturn(Optional.of(usuarioCliente));

        // ✅ 2. Añadimos la simulación que faltaba para findById del vendedor
        // (Esto es necesario si un ADMIN crea asignando vendedor, aunque
        // en este caso el rol es CLIENTE, es bueno tenerlo por si acaso)
        // O MÁS IMPORTANTE: si el DTO trae un vendedorId, se busca.
        given(usuarioRepository.findById(usuarioCliente.getId())).willReturn(Optional.of(usuarioCliente));


        // Simulamos el save() del repo de producto
        given(productoRepository.save(any(Producto.class))).willAnswer(invocation -> {
            Producto p = invocation.getArgument(0);
            p.setId(1L); // Simulamos ID asignado
            // Aseguramos que el vendedor se asignó correctamente en el servicio
            // Si el servicio asigna null, este mock devolverá null vendedorId
            if (p.getVendedor() != null) {
                p.getVendedor().setId(usuarioCliente.getId()); // Aseguramos ID del vendedor
            }
            return p;
        });

        // When (Llamamos al método de guardar SIN ID en el DTO)
        productoDTO.setId(null);
        ProductoDTO guardadoDTO = productoService.guardar(productoDTO);

        // Then (El DTO devuelto debe tener ID y los datos correctos)
        assertThat(guardadoDTO).isNotNull();
        assertThat(guardadoDTO.getId()).isEqualTo(1L);
        // Comprobamos que el vendedor asignado sea el correcto
        assertThat(guardadoDTO.getVendedorId()).isEqualTo(usuarioCliente.getId());

        verify(productoRepository).save(any(Producto.class));

        // ✅ 3. Limpiamos el SecurityContext al final del test (buena práctica)
        SecurityContextHolder.clearContext();
    }
    @Test
    void shouldUpdateAndReturnProductoDTOWhenUpdatingExistingProductoAndUserIsOwner() {
        // Given (Simulamos el usuario autenticado, y los find y save del repo)

        // 1. Simular Seguridad: El dueño está logueado
        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn(usuarioCliente.getCorreo()); // Dueño
        given(usuarioRepository.findByCorreo(usuarioCliente.getCorreo())).willReturn(Optional.of(usuarioCliente));

        // 2. Simular que el producto a actualizar existe
        given(productoRepository.findById(productoExistente.getId())).willReturn(Optional.of(productoExistente));

        given(usuarioRepository.findById(usuarioCliente.getId())).willReturn(Optional.of(usuarioCliente));
        // 3. Simular el guardado (save) - devolverá el mismo objeto que recibe
        given(productoRepository.save(any(Producto.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When (Llamamos a guardar con un DTO que tiene ID y datos cambiados)
        productoDTO.setId(productoExistente.getId()); // ID para indicar actualización
        productoDTO.setNombre("Lomo Saltado ACTUALIZADO"); // Dato cambiado
        productoDTO.setPrecio(30.0); // Otro dato cambiado

        ProductoDTO actualizadoDTO = productoService.guardar(productoDTO);

        // Then (El DTO devuelto debe tener los datos actualizados)
        assertThat(actualizadoDTO).isNotNull();
        assertThat(actualizadoDTO.getId()).isEqualTo(productoExistente.getId());
        assertThat(actualizadoDTO.getNombre()).isEqualTo("Lomo Saltado ACTUALIZADO");
        assertThat(actualizadoDTO.getPrecio()).isEqualTo(30.0);
        assertThat(actualizadoDTO.getVendedorId()).isEqualTo(usuarioCliente.getId()); // Dueño no cambia

        // Verificar que save fue llamado
        verify(productoRepository).save(any(Producto.class));

        SecurityContextHolder.clearContext(); // Limpiar contexto
    }

    @Test
    void shouldThrowAccesoNoAutorizadoExceptionWhenUpdatingProductoAndUserIsNotOwnerOrAdmin() {
        // Given (Simulamos un usuario DIFERENTE al dueño)
        Usuario usuarioNoPropietario = new Usuario();
        usuarioNoPropietario.setId(99L);
        usuarioNoPropietario.setCorreo("otro@test.com");
        usuarioNoPropietario.setRol(Rol.CLIENTE); // Es cliente, pero no el dueño

        // 1. Simular Seguridad: El NO dueño está logueado
        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn(usuarioNoPropietario.getCorreo());
        given(usuarioRepository.findByCorreo(usuarioNoPropietario.getCorreo())).willReturn(Optional.of(usuarioNoPropietario));

        // 2. Simular que el producto existe
        given(productoRepository.findById(productoExistente.getId())).willReturn(Optional.of(productoExistente));

        // When & Then (Esperamos la excepción al intentar guardar)
        productoDTO.setId(productoExistente.getId()); // ID para indicar actualización
        assertThrows(AccesoNoAutorizadoException.class, () -> {
            productoService.guardar(productoDTO);
        });

        // Verificar que save NUNCA fue llamado
        verify(productoRepository, never()).save(any(Producto.class));

        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAllowAdminToUpdateProductoWhenUserIsAdmin() {
        // Given (Simulamos un usuario ADMIN)
        Usuario usuarioAdmin = new Usuario();
        usuarioAdmin.setId(1L);
        usuarioAdmin.setCorreo("admin@test.com");
        usuarioAdmin.setRol(Rol.ADMIN); // Es ADMIN

        // 1. Simular Seguridad: El ADMIN está logueado
        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn(usuarioAdmin.getCorreo());
        given(usuarioRepository.findByCorreo(usuarioAdmin.getCorreo())).willReturn(Optional.of(usuarioAdmin));

        given(usuarioRepository.findById(usuarioCliente.getId())).willReturn(Optional.of(usuarioCliente));
        // 2. Simular que el producto existe
        given(productoRepository.findById(productoExistente.getId())).willReturn(Optional.of(productoExistente));

        // 3. Simular el save
        given(productoRepository.save(any(Producto.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When (Llamamos a guardar con un DTO que tiene ID)
        productoDTO.setId(productoExistente.getId());
        productoDTO.setNombre("Actualizado por Admin");

        // (No necesitamos assertThrows, esperamos que NO lance excepción)
        ProductoDTO actualizadoDTO = productoService.guardar(productoDTO);

        // Then (Verificamos que se actualizó y save fue llamado)
        assertThat(actualizadoDTO).isNotNull();
        assertThat(actualizadoDTO.getNombre()).isEqualTo("Actualizado por Admin");
        verify(productoRepository).save(any(Producto.class));

        SecurityContextHolder.clearContext();
    }


    // --- Tests para eliminar ---

    @Test
    void shouldDeleteProductoWhenUserIsOwner() {
        // Given (Simulamos al dueño logueado y que el producto existe)
        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn(usuarioCliente.getCorreo()); // Dueño
        given(usuarioRepository.findByCorreo(usuarioCliente.getCorreo())).willReturn(Optional.of(usuarioCliente));
        given(productoRepository.findById(productoExistente.getId())).willReturn(Optional.of(productoExistente));
        // No necesitamos simular deleteById, solo verificar que se llame

        // When (Llamamos al método eliminar)
        productoService.eliminar(productoExistente.getId());

        // Then (Verificamos que deleteById fue llamado con el ID correcto)
        verify(productoRepository).deleteById(productoExistente.getId());

        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldThrowAccesoNoAutorizadoExceptionWhenDeletingProductoAndUserIsNotOwnerOrAdmin() {
        // Given (Simulamos un usuario NO dueño)
        Usuario usuarioNoPropietario = new Usuario();
        usuarioNoPropietario.setId(99L);
        usuarioNoPropietario.setCorreo("otro@test.com");
        usuarioNoPropietario.setRol(Rol.CLIENTE);

        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn(usuarioNoPropietario.getCorreo());
        given(usuarioRepository.findByCorreo(usuarioNoPropietario.getCorreo())).willReturn(Optional.of(usuarioNoPropietario));
        given(productoRepository.findById(productoExistente.getId())).willReturn(Optional.of(productoExistente));

        // When & Then (Esperamos la excepción al intentar eliminar)
        assertThrows(AccesoNoAutorizadoException.class, () -> {
            productoService.eliminar(productoExistente.getId());
        });

        // Verificar que deleteById NUNCA fue llamado
        verify(productoRepository, never()).deleteById(anyLong());

        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAllowAdminToDeleteProductoWhenUserIsAdmin() {
        // Given (Simulamos un ADMIN logueado y que el producto existe)
        Usuario usuarioAdmin = new Usuario();
        usuarioAdmin.setId(1L);
        usuarioAdmin.setCorreo("admin@test.com");
        usuarioAdmin.setRol(Rol.ADMIN);

        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn(usuarioAdmin.getCorreo());
        given(usuarioRepository.findByCorreo(usuarioAdmin.getCorreo())).willReturn(Optional.of(usuarioAdmin));
        given(productoRepository.findById(productoExistente.getId())).willReturn(Optional.of(productoExistente));

        // When (Llamamos a eliminar)
        productoService.eliminar(productoExistente.getId());

        // Then (Verificamos que deleteById SÍ fue llamado)
        verify(productoRepository).deleteById(productoExistente.getId());

        SecurityContextHolder.clearContext();
    }

    // --- Test para buscarYFiltrar (Ejemplo básico) ---
    @Test
    void shouldReturnFilteredProductosAsDTOsWhenCallingBuscarYFiltrar() {
        // Given (Simulamos que el repo devuelve una lista de productos)
        List<Producto> productosFiltrados = List.of(productoExistente);
        given(productoRepository.buscarProductos(anyString(), anyDouble())).willReturn(productosFiltrados);

        // When (Llamamos al método del servicio)
        List<ProductoDTO> resultadoDTO = productoService.buscarYFiltrar("Lomo", 30.0);

        // Then (El resultado debe ser una lista de DTOs)
        assertThat(resultadoDTO).isNotNull();
        assertThat(resultadoDTO).hasSize(1);
        assertThat(resultadoDTO.get(0).getId()).isEqualTo(productoExistente.getId());
        assertThat(resultadoDTO.get(0).getNombre()).isEqualTo(productoExistente.getNombre());

        // Verificar que buscarProductos fue llamado con los parámetros correctos
        verify(productoRepository).buscarProductos("Lomo", 30.0);
    }

    // (Aquí seguirían los tests para actualizar, eliminar, etc.)
}