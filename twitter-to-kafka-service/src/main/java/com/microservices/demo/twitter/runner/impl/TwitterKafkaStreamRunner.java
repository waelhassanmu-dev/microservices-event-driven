package com.microservices.demo.twitter.runner.impl;

import com.microservices.demo.twitter.config.DataConfig;
import com.microservices.demo.twitter.listener.TwitterKafkaStatusListener;
import com.microservices.demo.twitter.runner.StreamRunner;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.FilterQuery;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import java.util.Arrays;

/**
 * This use Twitter V1 Streaming APIs and its @Deprecated
 */

@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "false", matchIfMissing = true)
public class TwitterKafkaStreamRunner implements StreamRunner {

    private final DataConfig config;
    private final TwitterKafkaStatusListener listener;
    private TwitterStream twitterStream;
    @Override
    public void start() throws TwitterException {
        twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(listener);
        addFilter();

    }

    @PreDestroy
    public void shutDown() {
        if (twitterStream != null) {
            log.info("closing twitter stream !!");
            twitterStream.shutdown();
        }
    }

    private void addFilter() {
        String[] keywords = config.getTwitterKeywords().toArray(new String[0]);
        FilterQuery filterQuery = new FilterQuery(keywords);
        twitterStream.filter(filterQuery);
        log.info("starting filtering twitter streams for keywords {}", Arrays.toString(keywords));
    }
}
