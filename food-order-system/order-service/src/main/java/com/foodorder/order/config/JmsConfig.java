package com.foodorder.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJms
@Profile("!local")
public class JmsConfig {

    @Bean
    public MessageConverter messageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        
        // Define a type ID mapping to help deserializers map JSON class attributes
        Map<String, Class<?>> typeIdMappings = new HashMap<>();
        typeIdMappings.put("OrderCreatedEvent", com.foodorder.order.dto.OrderCreatedEvent.class);
        converter.setTypeIdMappings(typeIdMappings);
        
        return converter;
    }
}
