package com.proyecto_backend.FoodHub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "kioskos")

public class Kiosko {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,  unique = true)
    private String nombre;

    @Column(nullable = false)
    private String ubicacion;

    @Column(nullable = false)
    private String horario;

    @Column(nullable = false)
    private String descripcion;



    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "kiosko_producto",
            joinColumns = @JoinColumn(name = "kiosko_id"),
            inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    @JsonIgnore
    private List<Producto> productos = new ArrayList<>();


}
