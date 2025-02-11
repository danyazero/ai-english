package org.zero.aienglish.config;

import com.google.gson.JsonSerializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.zero.aienglish.model.NotificationDTO;
import org.zero.aienglish.utils.KafkaPropsProvider;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    @Bean
    public ProducerFactory<String, NotificationDTO> paymentProducerFactory() {
        return KafkaPropsProvider.producer(NotificationDTO.class);
    }

    @Bean
    public KafkaTemplate<String, NotificationDTO> paymentKafkaTemplate(
            ProducerFactory<String, NotificationDTO> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic notificationKafkaTopic() {
        return new NewTopic("notification", 1, (short) 1);
    }
}
