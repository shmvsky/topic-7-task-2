package ru.shmvsky.log4j.kafka.appender;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.SerializedLayout;
import org.apache.logging.log4j.core.util.Booleans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Plugin(
        name = "KafkaAppender",
        category = "Core",
        elementType = "Appender",
        printObject = true
)
public final class KafkaAppender extends AbstractAppender {

    private final KafkaProducer<String, String> producer;
    private final String topic;

    private KafkaAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, KafkaProducer<String, String> producer, String topic) {
        super(name, filter, layout, ignoreExceptions);
        this.producer = producer;
        this.topic = topic;
    }

    @PluginFactory
    public static KafkaAppender createAppender(@PluginAttribute("name") final String name,
                                               @PluginElement("Filter") final Filter filter,
                                               @PluginAttribute("topic") final String topic,
                                               @PluginAttribute("ignoreExceptions") final String ignore,
                                               @PluginElement("Layout") Layout<? extends Serializable> layout,
                                               @PluginElement("Properties") final Property[] properties) {

        boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);

        Map<String, Object> props = new HashMap<>();
        for (Property property : properties) {
            props.put(property.getName(), property.getValue());
        }
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        if(layout == null) {
            layout = SerializedLayout.createLayout();
        }

        return new KafkaAppender(name, filter, layout, ignoreExceptions, producer, topic);

    }

    @Override
    public void stop() {
        super.stop();
        producer.close();
    }

    @Override
    public void append(LogEvent event) {
        try {
            producer.send(new ProducerRecord<>(topic, getLayout().toSerializable(event).toString()));
        } catch (final Exception e) {
            error("Unable to write to Kafka in appender [" + getName() + "]", event, e);
        }
    }
}
