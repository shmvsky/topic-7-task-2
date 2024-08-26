package ru.shmvsky;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Log4jKafkaAppenderSampleApplication {

    public static void main(String[] args) {

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

        LogsProducer logsProducerTask = new LogsProducer("Log4jKafkaAppender");
        LogsConsumer logsConsumerTask = new LogsConsumer("localhost:9092");

        executorService.scheduleAtFixedRate(logsProducerTask, 0, 1000, TimeUnit.MILLISECONDS);
        executorService.scheduleAtFixedRate(logsConsumerTask, 10000, 10000, TimeUnit.MILLISECONDS);
    }

}
