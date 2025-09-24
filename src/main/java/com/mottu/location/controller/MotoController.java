package com.mottu.location.controller;

import com.mottu.location.model.Moto;
import com.mottu.location.repository.MotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/motos") // Novo endpoint base para motos
public class MotoController {

    @Autowired
    private MotoRepository motoRepository;

    // GET (Listar todas as motos)
    @GetMapping
    public List<Moto> getAllMotos() {
        return motoRepository.findAll();
    }

    // GET (Buscar moto por ID)
    @GetMapping("/{id}")
    public ResponseEntity<Moto> getMotoById(@PathVariable Long id) {
        return motoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET (Buscar moto por PLACA)
    @GetMapping("/placa/{placa}")
    public ResponseEntity<Moto> getMotoByPlaca(@PathVariable String placa) {
        return motoRepository.findByPlaca(placa)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST (Criar nova moto)
    @PostMapping
    public ResponseEntity<Moto> createMoto(@RequestBody Moto moto) {
        if (moto.getPlaca() == null || moto.getPlaca().isEmpty()) {
            return ResponseEntity.badRequest().body(null); // Ou lançar uma exceção
        }
        // Verifica se já existe moto com essa placa
        if (motoRepository.findByPlaca(moto.getPlaca()).isPresent()) {
             throw new ResponseStatusException(HttpStatus.CONFLICT, "Moto com esta placa já existe: " + moto.getPlaca());
        }
        Moto savedMoto = motoRepository.save(moto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMoto);
    }

    // PUT (Atualizar moto por ID)
    @PutMapping("/{id}")
    public ResponseEntity<Moto> updateMoto(@PathVariable Long id, @RequestBody Moto motoDetails) {
        return motoRepository.findById(id)
                .map(existingMoto -> {
                    existingMoto.setPlaca(motoDetails.getPlaca());
                    existingMoto.setModelo(motoDetails.getModelo());
                    existingMoto.setFabricante(motoDetails.getFabricante());
                    // Não mexer nas localizações por aqui, a menos que seja a intenção
                    Moto updatedMoto = motoRepository.save(existingMoto);
                    return ResponseEntity.ok(updatedMoto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE (Deletar moto por ID)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMoto(@PathVariable Long id) {
        if (!motoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        motoRepository.deleteById(id);
        // Lembre-se que CascadeType.ALL vai deletar as localizações associadas.
        return ResponseEntity.noContent().build();
    }
}