package ru.shmvsky;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import ru.shmvsky.log4j.kafka.appender.KafkaAppender;

public class LogsProducer implements Runnable {

    private final Logger logger;
    private long counter = 0;
    
    public LogsProducer(String appenderName) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        KafkaAppender appender = (KafkaAppender) config.getAppenders().get(appenderName);
    
        logger = (Logger) LogManager.getLogger(LogsProducer.class);
        logger.addAppender(appender);
        logger.setLevel(Level.INFO);
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            logger.info(String.format("New log message №%d", ++counter));
        }
    }
}
