package com.example.catalogservice.messagequeue;

import com.example.catalogservice.reopository.CatalogEntity;
import com.example.catalogservice.reopository.CatalogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class KafkaConsumer {
    CatalogRepository repository;

    public KafkaConsumer(CatalogRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "example-order-topic")
    public void updateQuantity(String kafkaMessage) {
        log.info(String.format("Kafka Message =====> %s", kafkaMessage));

        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Optional<CatalogEntity> entity = repository.findByProductId((String)map.get("productId"));
        if (entity.isEmpty()) {
            log.error("productId is Empty");
        }
        else {
            entity.get().setStock(entity.get().getStock() - (Integer)map.get("quantity"));
            repository.save(entity.get());
        }
    }
}
