package com.example.pmddemo;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;

@SpringJUnitConfig
@SpringBootTest
@Testcontainers
class PmdDemoApplicationTests {

    @Container
    public static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer()
            .withExposedPorts(5672, 15672); // Expose client and admin end points
    static String address = "hot.topic";
    @Autowired
    RabbitAdmin rabbitAdmin;
    @Autowired
    HotTopic client;
    String hotNews = "News flash";

    @Test
    void contextLoads() {
    }


    @BeforeEach
    void makeQueue() {
        rabbitAdmin.declareQueue(new Queue(client.address, true, false, false));
    }

    @Test
    void receive() {
        client.send(hotNews);
        await().atMost(5, SECONDS).until(() -> client.receive() != null);
        Object o = client.lastIheard;
        assertThat(o).isInstanceOf(byte[].class);
    }

    @Test
    void receiveAndConvert() {
        client.send(hotNews);
        await().atMost(5, SECONDS).until(() -> client.receiveAndConvert() != null);
        Object o = client.lastIheard;
        assertThat(o).isInstanceOf(String.class);
    }

    @Configuration
    static class TestConfiguration {

        @Bean
        public ConnectionFactory rabbitMQConnectionFactory() {
            URI uri = URI.create(rabbitMQContainer.getAmqpUrl());
            return new CachingConnectionFactory(uri);
        }

        @Bean
        public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
            return new RabbitAdmin(connectionFactory);
        }

        @Bean
        public HotTopic hotTopic(ConnectionFactory connectionFactory) {
            return new HotTopic(connectionFactory);
        }

    }
}
