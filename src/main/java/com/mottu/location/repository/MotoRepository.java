package com.mottu.location.repository;

import com.mottu.location.model.Moto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // Para busca por placa

public interface MotoRepository extends JpaRepository<Moto, Long> {
    Optional<Moto> findByPlaca(String placa); // Método para buscar moto pela placa
}