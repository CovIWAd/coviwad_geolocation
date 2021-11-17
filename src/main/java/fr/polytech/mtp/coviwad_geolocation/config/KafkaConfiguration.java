package fr.polytech.mtp.coviwad_geolocation.config;

import fr.polytech.mtp.coviwad_geolocation.models.Geolocation;
import fr.polytech.mtp.coviwad_geolocation.models.UserPositive;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String serverName;

    @Bean
    public NewTopic geolocationAdded() {
        return TopicBuilder.name("geolocation_added").config(TopicConfig.RETENTION_MS_CONFIG, "1").build();
    }

    @Bean
    public NewTopic userPositive() {
        return TopicBuilder.name("user_positive").config(TopicConfig.RETENTION_MS_CONFIG, "1").build();
    }



    @Bean
    public ProducerFactory<String, String> producerFactoryString() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplateString() {
        return new KafkaTemplate<>(producerFactoryString());
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "group_id");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        return factory;
    }

    @Bean
    public ConsumerFactory<String, Geolocation> geolocationConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.GROUP_ID_CONFIG,"covid-alert");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, serverName);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(Geolocation.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Geolocation> geolocationKafkaListener() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Geolocation>();
        factory.setConsumerFactory(geolocationConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, UserPositive> userConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.GROUP_ID_CONFIG,"covid-alert");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, serverName);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(UserPositive.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserPositive> userKafkaListener() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, UserPositive>();
        factory.setConsumerFactory(userConsumerFactory());
        return factory;
    }

}
