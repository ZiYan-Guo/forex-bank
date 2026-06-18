package com.forex.bookkeeping;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@EnableAutoConfiguration(exclude = {
        RedisAutoConfiguration.class,
        org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration.class,
        org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration.class
})
@ComponentScan("com.forex.bookkeeping")
public class TestApplication {
}
