package com.mottu.location.model;

import jakarta.persistence.*;
import java.util.List; // Para a lista de localizações
import com.fasterxml.jackson.annotation.JsonManagedReference; // Para evitar recursão infinita no JSON

@Entity
@Table(name = "moto")
public class Moto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false) // Placa deve ser única e não nula
    private String placa;

    private String modelo;
    private String fabricante;

    // Relacionamento: Uma Moto para Muitas MotoLocation
    // mappedBy = "moto" indica que o lado "moto" na entidade MotoLocation é o dono do relacionamento.
    // cascade = CascadeType.ALL significa que operações (como salvar, deletar) na Moto se propagam para as MotoLocations associadas.
    // fetch = FetchType.LAZY para otimizar (só carrega as localizações quando explicitamente solicitado).
    @OneToMany(mappedBy = "moto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference // Ajuda a evitar recursão infinita ao serializar para JSON
    private List<MotoLocation> locations;

    // Construtores
    public Moto() {
    }

    public Moto(String placa, String modelo, String fabricante) {
        this.placa = placa;
        this.modelo = modelo;
        this.fabricante = fabricante;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getFabricante() { return fabricante; }
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }
    public List<MotoLocation> getLocations() { return locations; }
    public void setLocations(List<MotoLocation> locations) { this.locations = locations; }
}