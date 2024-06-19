package com.example.ratelimiterservice.bucket;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.locks.ReentrantLock;

@Getter
@Setter
public class TokenBucket implements Bucket{
    private int tokens;
    private final int maxTokens;
    private final int refillTokens;
    private final long refillIntervalMillis;
    private long lastRefillTimestamp;
    private final ReentrantLock lock = new ReentrantLock();

    public TokenBucket(int maxTokens, int refillTokens, long refillIntervalMillis) {
        this.tokens = maxTokens;
        this.lastRefillTimestamp = System.currentTimeMillis();
        this.maxTokens = maxTokens;
        this.refillTokens = refillTokens;
        this.refillIntervalMillis = refillIntervalMillis;
    }


    @Override
    public boolean grant() {
        lock.lock();
        try{
            long currentTimestamp = System.currentTimeMillis();
            System.out.println("Request sent to token bucket at " + currentTimestamp);
            if(currentTimestamp - lastRefillTimestamp >= refillIntervalMillis) {
                tokens = Math.min(maxTokens, tokens + refillTokens);
                lastRefillTimestamp = currentTimestamp;
            }
            if(tokens > 0){
                tokens --;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
}
