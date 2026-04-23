package com.example.clinica.repository;

import com.example.clinica.entity.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    // AQUÍ PUEDES EMPEZAR A ESCRIBIR TUS QUERY METHODS
    // Ejemplo: List<Mascota> findByEspecie(String especie);
    
}
