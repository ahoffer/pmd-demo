package com.example.pmddemo;


import org.springframework.jms.core.JmsTemplate;

import java.util.concurrent.Callable;

public class HotTopic {

    Callable<Object> x;
    JmsTemplate template;

    public void foobar(String message) throws Exception {

        x = () -> 66;

        template.convertAndSend(

                "hot.topic",

                x.call());
    }
}


