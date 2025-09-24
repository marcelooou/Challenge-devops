package com.mottu.location.repository;

import com.mottu.location.model.MotoLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MotoLocationRepository extends JpaRepository<MotoLocation, Long> {

    // GARANTA QUE ESTA LINHA EXISTE E ESTÁ CORRETA:
    List<MotoLocation> findByMoto_Placa(String placaDaMoto);

    // Opcional, se também quiser buscar pelo ID da Moto (que é Long):
    // List<MotoLocation> findByMoto_Id(Long idDaMoto);

    // O método antigo que causava o erro anterior já deve ter sido removido ou comentado:
    // List<MotoLocation> findByMotoId(String motoId);
}