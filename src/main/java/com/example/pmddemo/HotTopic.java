package com.example.pmddemo;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class HotTopic {

    final ConnectionFactory connectionFactory;
    final RabbitTemplate template;
    final String address = "hot.topic";
    Object lastIheard;

    public HotTopic(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.template = new RabbitTemplate(connectionFactory);
    }

    public void send(String message) {
        template.convertAndSend(address, message);
    }


    // LISTENER IS NOT WORKING IN THE TESTS
    // Throws an exception when receive() is called if the address is not defined in the broker
    @RabbitListener(queues = "hot.topic")
    void receive(Message message) {
        lastIheard = message.getBody();
    }

    Object receiveAndConvert() {
        lastIheard = template.receiveAndConvert(address);
        return lastIheard;
    }

    Object receive() {
        Message message = template.receive(address);
        if (message != null)
            lastIheard = message.getBody();
        return lastIheard;
    }
}


