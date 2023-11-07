package com.microservices.demo.twitter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "twitter-to-kafka-service")
public class DataConfig {

    private List<String> twitterKeywords;
    private Boolean enableMockTweets;
    private Integer mockMinTweetLength;
    private Integer mockMaxTweetLength;
    private Long mockSleepMs;

}

