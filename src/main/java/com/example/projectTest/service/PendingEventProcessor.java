package com.example.projectTest.service;

import com.example.projectTest.entity.PendingKafkaEvent;
import com.example.projectTest.kafka.UserEventProducer;
import com.example.projectTest.repository.PendingKafkaEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PendingEventProcessor {

    private final PendingKafkaEventRepository repository;
    private final UserEventProducer eventProducer;

    @Scheduled(fixedDelay = 30000) // Каждые 30 секунд
    public void processPendingEvents() {
        log.info("Начинаем обработку отложенных событий Kafka");

        List<PendingKafkaEvent> pendingEvents = repository.findByProcessedFalseAndRetryCountLessThan(5);

        for (PendingKafkaEvent event : pendingEvents) {
            try {
                eventProducer.send(event.getTitle(), event.getEmail());
                event.setProcessed(true);
                repository.save(event);
                log.info("Событие успешно отправлено в Kafka. ID: {}", event.getId());
            } catch (Exception e) {
                log.warn("Повторная отправка события ID {} не удалась. Попытка №{}",
                        event.getId(), event.getRetryCount() + 1, e);
                event.setRetryCount(event.getRetryCount() + 1);
                repository.save(event);
            }
        }
    }

    @Scheduled(cron = "0 0 2 * * ?") // Каждый день в 02:00
    public void cleanupOldEvents() {
        repository.deleteByCreatedAtBefore(LocalDateTime.now().minusDays(7));
        log.info("Удалены старые события (старше 7 дней)");
    }
}
