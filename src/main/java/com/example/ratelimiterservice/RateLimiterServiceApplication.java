package com.example.ratelimiterservice;

import com.example.ratelimiterservice.factory.RateLimiterFactory;
import com.example.ratelimiterservice.ratelimiter.RateLimiter;
import com.example.ratelimiterservice.ratelimiter.TokenBucketRateLimiter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class RateLimiterServiceApplication {

    public static void main(String[] args) throws InterruptedException {
        RateLimiter tokenBucketRateLimiter = RateLimiterFactory.createRateLimiter("TokenBucket", 10, 500);
        RateLimiter leakyBucketRateLimiter = RateLimiterFactory.createRateLimiter("LeakyBucket", 5, 1000);
        testRateLimiting(tokenBucketRateLimiter, "Token Bucket");
        //testRateLimiting(leakyBucketRateLimiter, "Leaky Bucket");
        //SpringApplication.run(RateLimiterServiceApplication.class, args);
    }

    private static void testRateLimiting(RateLimiter rateLimiter, String type) throws InterruptedException {
        String userId = "user123";
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        System.out.println("Testing " + type + " with concurrency");
        for(int i = 0; i < 50; i++){
            int finalI = i;
            executorService.submit(() -> {
                try {
                    if(finalI > 10)
                        Thread.sleep(300);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                boolean allowed = rateLimiter.allowRequest(userId);
                System.out.println("Request: " + finalI + " " +allowed);
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println();
    }

}
