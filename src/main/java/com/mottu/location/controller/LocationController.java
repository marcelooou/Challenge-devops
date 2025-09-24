package com.mottu.location.controller;

import com.mottu.location.model.Moto;
import com.mottu.location.model.MotoLocation;
import com.mottu.location.repository.MotoLocationRepository;
import com.mottu.location.repository.MotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private MotoLocationRepository locationRepository;

    @Autowired
    private MotoRepository motoRepository;

    // --- GET (Listar todas as localizações) ---
    @GetMapping
    public List<MotoLocation> getAllLocations() {
        return locationRepository.findAll();
    }

    // --- GET (Buscar localização específica pelo SEU ID) ---
    @GetMapping("/{id}")
    public ResponseEntity<MotoLocation> getLocationById(@PathVariable Long id) {
        return locationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- GET (Listar todas as localizações de uma MOTO específica pela PLACA da moto) ---
    @GetMapping("/moto/placa/{placa}")
    public ResponseEntity<List<MotoLocation>> getLocationsByMotoPlaca(@PathVariable String placa) {
        // Verifica se a moto com a placa dada existe primeiro (opcional, mas bom para feedback)
        if (!motoRepository.findByPlaca(placa).isPresent()) {
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Moto com placa " + placa + " não encontrada.");
        }
        List<MotoLocation> locations = locationRepository.findByMoto_Placa(placa); // USA O NOVO MÉTODO DO REPOSITÓRIO
        if (locations == null || locations.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList()); // Retorna lista vazia se não houver localizações
        }
        return ResponseEntity.ok(locations);
    }

    // --- POST (Criar nova localização para uma MOTO existente pela PLACA da moto) ---
    @PostMapping("/moto/placa/{placa}")
    public ResponseEntity<MotoLocation> createLocationForMoto(@PathVariable String placa, @RequestBody LocationRequestPayload locationPayload) {
        Optional<Moto> motoOptional = motoRepository.findByPlaca(placa);
        if (motoOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Moto com placa " + placa + " não encontrada. Crie a moto primeiro para associar uma localização.");
        }

        Moto moto = motoOptional.get();
        MotoLocation newLocation = new MotoLocation();
        newLocation.setMoto(moto);
        newLocation.setLatitude(locationPayload.getLatitude());
        newLocation.setLongitude(locationPayload.getLongitude());
        newLocation.setTimestamp(Instant.now());

        MotoLocation savedLocation = locationRepository.save(newLocation);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLocation);
    }

    // --- PUT (Atualizar uma localização específica pelo SEU ID) ---
    @PutMapping("/{id}")
    public ResponseEntity<MotoLocation> updateLocation(@PathVariable Long id, @RequestBody LocationRequestPayload updatedLocationData) {
        Optional<MotoLocation> existingLocationOptional = locationRepository.findById(id);
        if (existingLocationOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MotoLocation existingLocation = existingLocationOptional.get();
        existingLocation.setLatitude(updatedLocationData.getLatitude());
        existingLocation.setLongitude(updatedLocationData.getLongitude());
        existingLocation.setTimestamp(Instant.now());

        MotoLocation savedLocation = locationRepository.save(existingLocation);
        return ResponseEntity.ok(savedLocation);
    }

    // --- DELETE (Deletar uma localização específica pelo SEU ID) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        if (!locationRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        locationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // DTO para o corpo da requisição de POST e PUT de Location
    private static class LocationRequestPayload {
        private double latitude;
        private double longitude;

        // Getters e Setters (necessários para o Jackson deserializar o JSON)
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
    }
}