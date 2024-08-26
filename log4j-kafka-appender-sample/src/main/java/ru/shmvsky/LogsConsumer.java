package ru.shmvsky;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class LogsConsumer implements Runnable {

    private final KafkaConsumer<String, String> consumer;

    public LogsConsumer(String bootstrapServers) {

        Properties props = new Properties();
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "logs-consumer");
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("logs"));
    }

    @Override
    public void run() {
        try {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            System.out.println(records.count());
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("New log received: %s", record.value());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during consuming logs", e.getCause());
        }
    }
}
