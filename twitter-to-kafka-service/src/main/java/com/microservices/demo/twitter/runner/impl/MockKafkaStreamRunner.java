package com.microservices.demo.twitter.runner.impl;

import com.microservices.demo.twitter.config.DataConfig;
import com.microservices.demo.twitter.exception.ServiceException;
import com.microservices.demo.twitter.listener.TwitterKafkaStatusListener;
import com.microservices.demo.twitter.runner.StreamRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This use Mock Data to simulate the Streaming
 */

@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "true")
public class MockKafkaStreamRunner implements StreamRunner {

    private final DataConfig config;
    private final TwitterKafkaStatusListener listener;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String[] WORDS = new String[]{
            "Lorem",
            "ipsum",
            "dolor",
            "sit",
            "amet",
            "consectetuer",
            "adipiscing",
            "elit",
            "Maecenas",
            "porttitor",
            "congue",
            "massa",
            "Fusce",
            "posuere",
            "magna",
            "sed",
            "pulvinar",
            "ultricies",
            "purus",
            "lectus",
            "malesuada",
            "libero"
    };

    private static final String tweetAsRawJson = "{" +
            "\"created_at\":\"{0}\"," +
            "\"id\":\"{1}\"," +
            "\"text\":\"{2}\"," +
            "\"user\":{\"id\":\"{3}\"}" +
            "}";

    private static final String TWITTER_STATUS_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";
    @Override
    public void start() throws TwitterException {
        String[] keywords = config.getTwitterKeywords().toArray(new String[0]);
        int minTweetLength = config.getMockMinTweetLength();
        int maxTweetLength = config.getMockMaxTweetLength();
        long sleepTimeMs = config.getMockSleepMs();
        log.info("starting [mock] filtering twitter streams for keywords {}", Arrays.toString(keywords));

        simulateTwitterStream(keywords, minTweetLength, maxTweetLength, sleepTimeMs);

    }

    private void simulateTwitterStream(String[] keywords, int minTweetLength, int maxTweetLength, long sleepTimeMs) {

        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                while (true) {
                    String formattedTweetAsRawJson = getFormattedTweet(keywords, minTweetLength, maxTweetLength);
                    Status status = TwitterObjectFactory.createStatus(formattedTweetAsRawJson);
                    listener.onStatus(status);
                    sleep(sleepTimeMs);
                }
            } catch (TwitterException e) {
                log.error("error while creating twitter status !", e);
            }
        });

    }

    private void sleep(long sleepTimeMs) {
        try {
            Thread.sleep(sleepTimeMs);
        } catch (InterruptedException e) {
            throw new ServiceException("error while sleeping for waiting a new status to create !!, due {}", e.getCause());
        }
    }

    private String getFormattedTweet(String[] keywords, int minTweetLength, int maxTweetLength) {
        String[] params = new String[] {
                ZonedDateTime.now().format(
                        DateTimeFormatter.ofPattern(TWITTER_STATUS_DATE_FORMAT, Locale.ENGLISH)
                ),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
                getRandomTweetContext(keywords, minTweetLength, maxTweetLength),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),

        };

        return formatTweetAsJsonWithParams(params);

    }

    private String formatTweetAsJsonWithParams(String[] params) {
        String tweet = tweetAsRawJson;
        for (int i = 0; i < params.length; i++) {
            tweet = tweet.replace("{" + i + "}", params[i]);
        }

        return tweet;
    }

    private String getRandomTweetContext(String[] keywords, int minTweetLength, int maxTweetLength) {
        StringBuilder tweet = new StringBuilder();
        int tweetLength = RANDOM.nextInt(maxTweetLength - minTweetLength + 1) + minTweetLength;
        return constructRandomTweet(keywords, tweetLength, tweet);
    }

    private String constructRandomTweet(String[] keywords, int tweetLength, StringBuilder tweet) {
        for (int i = 0; i < tweetLength; i++) {
            tweet.append(WORDS[RANDOM.nextInt(WORDS.length)]).append(" ");
            if (i == tweetLength / 2) {
                tweet.append(keywords[RANDOM.nextInt(keywords.length)]).append(" ");
            }
        }

        return tweet.toString().trim();
    }

}
