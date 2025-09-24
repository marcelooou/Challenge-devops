package com.mottu.location.model;

import jakarta.persistence.*;
import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonBackReference; // Para evitar recursão infinita no JSON

@Entity
@Table(name = "moto_location")
public class MotoLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // private String motoId; // REMOVA ESTE CAMPO ANTIGO ou renomeie/reutilize se quiser manter por outro motivo

    private double latitude;
    private double longitude;
    private Instant timestamp;

    // Relacionamento: Muitas MotoLocation para Uma Moto
    // nullable = false garante que toda localização DEVE estar associada a uma moto.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "moto_id_fk", nullable = false) // Nome da coluna de chave estrangeira no banco
    @JsonBackReference // Ajuda a evitar recursão infinita ao serializar para JSON
    private Moto moto; // A moto à qual esta localização pertence

    // Getters e Setters (ajuste os existentes e adicione para 'moto')
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // Se você removeu 'motoId' como String, remova os getters/setters dele.
    // Caso contrário, ajuste.

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public Moto getMoto() { return moto; }
    public void setMoto(Moto moto) { this.moto = moto; }
}