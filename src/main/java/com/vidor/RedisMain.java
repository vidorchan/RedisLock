package com.vidor;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RedisMain {
    public static void main(String[] args) {
        SpringApplication.run(RedisMain.class, args);
    }

    @Bean
    public Redisson redisson() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.1.102:6379").setDatabase(0);
        return (Redisson) Redisson.create(config);
    }
}
