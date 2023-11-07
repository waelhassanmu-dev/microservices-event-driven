package com.microservices.demo.twitter;


import com.microservices.demo.twitter.config.DataConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class TwitterApplication implements CommandLineRunner {

    private final DataConfig config;
    public static void main(String[] args) {
        SpringApplication.run(TwitterApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info(config.getTwitterKeywords().toString());
    }
}
