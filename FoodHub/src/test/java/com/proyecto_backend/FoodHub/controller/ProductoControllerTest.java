package com.proyecto_backend.FoodHub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto_backend.FoodHub.dto.ProductoDTO;
import com.proyecto_backend.FoodHub.exception.ResourceNotFoundException; // Importar
import com.proyecto_backend.FoodHub.repository.UsuarioRepository;
import com.proyecto_backend.FoodHub.security.JwtUtil;
import com.proyecto_backend.FoodHub.security.SecurityConfig;
import com.proyecto_backend.FoodHub.security.UserInfoUserDetailsService;
import com.proyecto_backend.FoodHub.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections; // Importar

import static org.mockito.ArgumentMatchers.*; // Importar any() y anyLong()
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing; // Para mockear métodos void
import static org.mockito.Mockito.doThrow; // Para mockear excepciones en void
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.is;

@WebMvcTest(ProductoController.class)
@Import({SecurityConfig.class, UserInfoUserDetailsService.class, JwtUtil.class})
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @MockBean
    private UserInfoUserDetailsService userDetailsService;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private ProductoDTO productoDTO;
    private ProductoDTO productoDTOActualizado;

    @BeforeEach
    void setUp() {
        productoDTO = new ProductoDTO();
        productoDTO.setId(1L);
        productoDTO.setNombre("Lomo Saltado Test");
        productoDTO.setPrecio(22.0);
        productoDTO.setDescripcion("Desde Controller Test");
        // Asignamos un vendedor para probar la autorización
        productoDTO.setVendedorId(10L);

        productoDTOActualizado = new ProductoDTO();
        productoDTOActualizado.setId(1L);
        productoDTOActualizado.setNombre("Lomo Saltado ACTUALIZADO");
        productoDTOActualizado.setPrecio(25.0);
        productoDTOActualizado.setDescripcion("Actualizado");
        productoDTOActualizado.setVendedorId(10L);
    }

    // --- Tests GET By ID (ya existentes) ---
    @Test
    @WithMockUser
    void shouldReturnProductoDTOWhenGetById() throws Exception {
        given(productoService.buscarPorId(anyLong())).willReturn(productoDTO);
        mockMvc.perform(get("/api/v1/productos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is(productoDTO.getNombre())));
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundWhenGetByIdAndProductoDoesNotExist() throws Exception {
        given(productoService.buscarPorId(anyLong())).willThrow(new ResourceNotFoundException("No encontrado"));
        mockMvc.perform(get("/api/v1/productos/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    // --- Tests POST (ya existentes) ---
    @Test
    @WithMockUser(roles = "CLIENTE")
    void shouldCreateProductoAndReturnCreatedStatusWhenUserIsCliente() throws Exception {
        given(productoService.guardar(any(ProductoDTO.class))).willReturn(productoDTO); // Asumimos que devuelve el DTO con ID
        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is(productoDTO.getNombre())));
    }

    @Test
    @WithMockUser // Sin roles
    void shouldReturnForbiddenWhenCreatingProductoWithoutRequiredRole() throws Exception {
        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productoDTO)))
                .andExpect(status().isForbidden());
    }



    // --- ✅ Tests PUT (Nuevos) ---
    @Test
    @WithMockUser(roles = {"CLIENTE", "KIOSKERO", "ADMIN"}) // Cualquiera de estos puede actualizar (según PreAuthorize)
    void shouldUpdateProductoAndReturnOkWhenUserHasPermission() throws Exception {
        long productoId = 1L;
        // Given: Simulamos que el servicio actualiza y devuelve el DTO actualizado
        given(productoService.guardar(any(ProductoDTO.class))).willReturn(productoDTOActualizado);

        // When: Simulamos petición PUT con el ID y el DTO actualizado
        ResultActions response = mockMvc.perform(put("/api/v1/productos/{id}", productoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoDTOActualizado)));

        // Then: Esperamos 200 OK y el JSON actualizado
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(productoDTOActualizado.getId().intValue())))
                .andExpect(jsonPath("$.nombre", is(productoDTOActualizado.getNombre())))
                .andExpect(jsonPath("$.precio", is(productoDTOActualizado.getPrecio())));
    }

    @Test
    @WithMockUser // Sin roles específicos
    void shouldReturnForbiddenWhenUpdatingProductoWithoutPermission() throws Exception {
        long productoId = 1L;
        // Given: No necesitamos mockear el servicio, la seguridad actúa antes

        // When: Simulamos petición PUT
        ResultActions response = mockMvc.perform(put("/api/v1/productos/{id}", productoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoDTOActualizado)));

        // Then: Esperamos 403 Forbidden
        response.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN") // Un usuario con permiso
    void shouldReturnNotFoundWhenUpdatingNonExistentProducto() throws Exception {
        long productoIdQueNoExiste = 99L;
        // Given: Simulamos que el servicio lanza ResourceNotFoundException al intentar guardar
        given(productoService.guardar(any(ProductoDTO.class)))
                .willThrow(new ResourceNotFoundException("No encontrado para actualizar"));

        // When: Simulamos petición PUT
        ResultActions response = mockMvc.perform(put("/api/v1/productos/{id}", productoIdQueNoExiste)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoDTOActualizado))); // DTO con ID 99

        // Then: Esperamos 404 Not Found (convertido por @ResponseStatus)
        response.andExpect(status().isNotFound());
    }


    // --- ✅ Tests DELETE (Nuevos) ---
    @Test
    @WithMockUser(roles = {"CLIENTE", "ADMIN"}) // Roles permitidos por PreAuthorize
    void shouldDeleteProductoAndReturnNoContentWhenUserHasPermission() throws Exception {
        long productoId = 1L;
        // Given: Simulamos que el servicio.eliminar NO lanza excepciones
        // Para métodos void, usamos doNothing() o simplemente no mockeamos si no lanza excepción
        doNothing().when(productoService).eliminar(productoId);

        // When: Simulamos petición DELETE
        ResultActions response = mockMvc.perform(delete("/api/v1/productos/{id}", productoId));

        // Then: Esperamos 204 No Content
        response.andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "KIOSKERO") // Rol NO permitido para DELETE
    void shouldReturnForbiddenWhenDeletingProductoWithoutPermission() throws Exception {
        long productoId = 1L;
        // Given: No necesitamos mockear el servicio

        // When: Simulamos petición DELETE
        ResultActions response = mockMvc.perform(delete("/api/v1/productos/{id}", productoId));

        // Then: Esperamos 403 Forbidden
        response.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN") // Un usuario con permiso
    void shouldReturnNotFoundWhenDeletingNonExistentProducto() throws Exception {
        long productoIdQueNoExiste = 99L;
        // Given: Simulamos que servicio.eliminar lanza ResourceNotFoundException
        doThrow(new ResourceNotFoundException("No encontrado para eliminar")).when(productoService).eliminar(productoIdQueNoExiste);

        // When: Simulamos petición DELETE
        ResultActions response = mockMvc.perform(delete("/api/v1/productos/{id}", productoIdQueNoExiste));

        // Then: Esperamos 404 Not Found
        response.andExpect(status().isNotFound());
    }


    // --- ✅ Test GET /buscar (Nuevo) ---
    @Test
    @WithMockUser
    void shouldReturnFilteredProductosWhenGetBuscar() throws Exception {
        // Given: Simulamos que el servicio devuelve una lista (puede ser vacía o con datos)
        given(productoService.buscarYFiltrar(anyString(), nullable(Double.class)))
                .willReturn(Collections.singletonList(productoDTO)); // Devuelve lista con 1 elemento

        // When: Simulamos petición GET a /buscar con parámetros
        ResultActions response = mockMvc.perform(get("/api/v1/productos/buscar")
                .param("nombre", "Lomo")
                .param("precioMax", "25.0")
                .contentType(MediaType.APPLICATION_JSON));

        // Then: Esperamos 200 OK y un array JSON (incluso si está vacío)
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray()) // Verifica que la respuesta sea un array
                .andExpect(jsonPath("$.length()", is(1))) // Verifica que haya 1 elemento
                .andExpect(jsonPath("$[0].nombre", is(productoDTO.getNombre()))); // Verifica nombre del primer elemento
    }
}