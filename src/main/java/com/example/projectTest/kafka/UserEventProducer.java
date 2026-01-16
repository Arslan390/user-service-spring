package com.example.projectTest.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserEventProducer {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;


    public void send(String title, String email) {
        log.info("Отправка пользовательского события: {}", title);
        UserEvent event = UserEvent.builder()
                .title(title)
                .email(email)
                .build();

        kafkaTemplate.send("user-events-topic", email, event)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error("Не удалось отправить сообщение : {}", exception.getMessage());
                    } else {
                        log.info("Сообщение успешно отправлено offset : {}", result.getRecordMetadata().offset());
                    }
                });
    }
}
