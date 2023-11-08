package com.microservices.demo.twitter;


import com.microservices.demo.config.TwitterKafkaDataConfig;
import com.microservices.demo.twitter.runner.StreamRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import twitter4j.TwitterException;

/**
 * This for streaming data from Twitter, and put the data into a Kafka Topic
 */

@SpringBootApplication
@ComponentScan(basePackages = "com.microservices.demo")
@RequiredArgsConstructor
@Slf4j
public class TwitterApplication implements CommandLineRunner {

    private final TwitterKafkaDataConfig config;
    private final StreamRunner streamRunner;
    public static void main(String[] args) {
        SpringApplication.run(TwitterApplication.class, args);
    }

    @Override
    public void run(String... args) throws TwitterException {
        log.info(config.getTwitterKeywords().toString());
        streamRunner.start();
    }
}
