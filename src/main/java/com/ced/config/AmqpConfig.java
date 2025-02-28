package com.ced.config;

import com.ced.properties.AmqpProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

@Configuration
public class AmqpConfig implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(AmqpConfig.class);
    private final AmqpProperties amqpProperties;
    
    @Autowired
    private ConnectionFactory connectionFactory;

    public AmqpConfig(AmqpProperties amqpProperties) {
        this.amqpProperties = amqpProperties;
    }

    @Bean
    public Queue refreshQueue() {
        return new Queue(amqpProperties.getQueueName(), true);
    }

    @Bean
    public DirectExchange refreshExchange() {
        return new DirectExchange(amqpProperties.getExchangeName());
    }

    @Bean
    public Binding binding(Queue refreshQueue, DirectExchange refreshExchange) {
        return BindingBuilder.bind(refreshQueue)
                .to(refreshExchange)
                .with(amqpProperties.getRoutingKey());
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            logger.info("Inicializando conexão com RabbitMQ...");
            
            if (connectionFactory instanceof CachingConnectionFactory) {
                ((CachingConnectionFactory) connectionFactory).createConnection();
                logger.info("Conexão com RabbitMQ estabelecida com sucesso!");
            }
            

            logger.info("Conexão com RabbitMQ inicializada. Sistema pronto para enviar mensagens.");
            
        } catch (Exception e) {
            logger.error("Erro ao inicializar conexão com RabbitMQ", e);
        }
    }
}