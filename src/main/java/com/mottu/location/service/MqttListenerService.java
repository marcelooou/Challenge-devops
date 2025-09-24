package com.mottu.location.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mottu.location.model.Moto; // Importar Moto
import com.mottu.location.model.MotoLocation;
import com.mottu.location.repository.MotoLocationRepository;
import com.mottu.location.repository.MotoRepository; // Importar MotoRepository
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@MessageEndpoint
public class MqttListenerService {

    private static final Logger logger = LoggerFactory.getLogger(MqttListenerService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MotoLocationRepository locationRepository; // Renomeado para clareza

    @Autowired
    private MotoRepository motoRepository; // Injetar MotoRepository

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        try {
            String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
            Object payloadObject = message.getPayload();
            String payload;

            if (payloadObject instanceof byte[]) {
                payload = new String((byte[]) payloadObject, StandardCharsets.UTF_8);
            } else if (payloadObject instanceof String) {
                payload = (String) payloadObject;
            } else {
                logger.warn("Tipo de payload MQTT inesperado: {}", payloadObject.getClass().getName());
                return;
            }

            logger.info("Mensagem MQTT recebida - Tópico: {}, Payload: {}", topic, payload);

            // Extrair placa da moto do tópico. Ex: mottu/motos/PLACA123/location -> PLACA123
            String[] topicParts = topic.split("/");
            if (topicParts.length < 4 || !topicParts[0].equals("mottu") || !topicParts[1].equals("motos") || !topicParts[3].equals("location")) {
                logger.warn("Tópico MQTT em formato inesperado ou não corresponde ao padrão esperado: '{}'. Esperado: mottu/motos/PLACA/location", topic);
                return;
            }
            String placaDaMoto = topicParts[2]; // Pega a placa da moto do tópico

            // Buscar a Moto pela placa. Se não existir, cria uma nova.
            Moto moto = motoRepository.findByPlaca(placaDaMoto)
                    .orElseGet(() -> {
                        logger.info("Moto com placa {} não encontrada via MQTT, criando nova.", placaDaMoto);
                        Moto novaMoto = new Moto();
                        novaMoto.setPlaca(placaDaMoto);
                        novaMoto.setModelo("Modelo Desconhecido (via MQTT)"); // Defina valores padrão se desejar
                        novaMoto.setFabricante("Fabricante Desconhecido (via MQTT)");
                        return motoRepository.save(novaMoto);
                    });

            // Processar o payload JSON para obter latitude e longitude
            LocationPayload locationData = objectMapper.readValue(payload, LocationPayload.class);

            MotoLocation motoLocation = new MotoLocation();
            motoLocation.setMoto(moto); // Associa o objeto Moto à localização
            motoLocation.setLatitude(locationData.getLatitude());
            motoLocation.setLongitude(locationData.getLongitude());
            motoLocation.setTimestamp(Instant.now());

            locationRepository.save(motoLocation);
            logger.info("Localização salva para moto com placa: {}", placaDaMoto);

        } catch (MessagingException | IOException e) {
            logger.error("Erro ao processar mensagem MQTT (IOException ou MessagingException): {}", e.getMessage(), e);
        } catch (Exception e) { // Captura genérica para outros erros inesperados
            logger.error("Erro inesperado ao processar mensagem MQTT: {}", e.getMessage(), e);
        }
    }

    // Classe DTO auxiliar para o payload JSON da localização vinda do MQTT
    private static class LocationPayload {
        private double latitude;
        private double longitude;

        // Getters e Setters
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
    }
}