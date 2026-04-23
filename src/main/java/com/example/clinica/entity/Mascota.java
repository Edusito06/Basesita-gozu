package com.example.clinica.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mascota")
@Getter
@Setter
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String especie;

    private String raza;

    private Integer edad;

    private Boolean estado;

    // Relación Muchos a Uno con Dueno
    @ManyToOne
    @JoinColumn(name = "id_dueno")
    private Dueno dueno;
}
