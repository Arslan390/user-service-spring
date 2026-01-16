package com.example.projectTest.kafka;

import com.example.projectTest.dto.CreateUserDto;
import com.example.projectTest.entity.User;
import com.example.projectTest.repository.UserRepository;
import com.example.projectTest.service.UserService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, count = 1, controlledShutdown = true)
@SpringBootTest(properties = "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}")
public class ProducerIntegrationTest {

    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    Environment environment;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private KafkaMessageListenerContainer<String, UserEvent> container;
    private BlockingQueue<ConsumerRecord<String, UserEvent>> records;


    @BeforeAll
    public void setup() {
        DefaultKafkaConsumerFactory<String, Object> consumerFactory =
                new DefaultKafkaConsumerFactory<>(getConsumerProperties());
        ContainerProperties containerProperties = new ContainerProperties(environment.getProperty("user-events-topic-name"));

        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, UserEvent>) records::add);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @Test
    void testCreateUser_successfullySendsKafkaMessage() throws InterruptedException {
        CreateUserDto createUser = CreateUserDto.builder()
                .email("test@mail.ru")
                .name("Test")
                .age(18)
                .build();

        User user = User.builder()
                .id(1L)
                .email("test@mail.ru")
                .name("Test")
                .age(25)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(user);
        userService.create(createUser);

        ConsumerRecord<String, UserEvent> message = records.poll(3000, TimeUnit.MILLISECONDS);
        assertNotNull(message);
        assertNotNull(message.key());
        UserEvent userEvent = message.value();
        assertNotNull(userEvent, "Тело сообщения не должно быть null");
        assertEquals("CREATED", userEvent.getTitle(), "Тип события должен быть CREATED");
        assertEquals("test@mail.ru", userEvent.getEmail(), "Email в событии должен совпадать с переданным");
    }

    @Test
    void testDeleteUser_successfullySendsKafkaMessage() throws InterruptedException {
        User user = User.builder()
                .id(1L)
                .email("test@mail.ru")
                .name("Test")
                .age(25)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        userService.delete(1L);

        ConsumerRecord<String, UserEvent> message = records.poll(3000, TimeUnit.MILLISECONDS);
        assertNotNull(message, "Сообщение об удалении не получено в течение 3 секунд");
        assertNotNull(message.key(), "Ключ сообщения об удалении не должен быть null");

        UserEvent userEvent = message.value();
        assertNotNull(userEvent, "Тело сообщения об удалении не должно быть null");
        assertEquals("DELETED", userEvent.getTitle(), "Тип события должен быть DELETED");
        assertEquals("test@mail.ru", userEvent.getEmail(), "Email в событии удаления должен совпадать");
    }

    private Map<String, Object> getConsumerProperties() {
        return Map.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString(),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class,
                ConsumerConfig.GROUP_ID_CONFIG, environment.getProperty("spring.kafka.consumer.group-id"),
                JacksonJsonDeserializer.TRUSTED_PACKAGES, environment.getProperty("spring.kafka.consumer.properties.spring.json.trusted.packages"),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, environment.getProperty("spring.kafka.consumer.auto-offset-reset")
        );
    }

    @AfterAll
    void teardown() {
        container.stop();
    }
}
