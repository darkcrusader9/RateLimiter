package com.example.ratelimiterservice.bucket;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class TokenBucket implements Bucket {
    private final int maxTokens; // Maximum tokens the bucket can hold
    private final int refillRate; // Tokens added per second
    private final AtomicInteger currentTokens; // Current number of tokens
    private long lastRefillTime; // Last time tokens were added
    private final ReentrantLock lock = new ReentrantLock(); // Lock for thread safety

    public TokenBucket(int maxTokens, int refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.currentTokens = new AtomicInteger(maxTokens);
        this.lastRefillTime = System.nanoTime();
    }

    @Override
    public boolean grant() {
        lock.lock(); // Acquire the lock
        try {
            refillTokens();
            if (currentTokens.get() > 0) {
                currentTokens.decrementAndGet(); // Consume one token for the request
                return true; // Grant permission for the request
            }
            return false; // Deny request if no tokens are available
        } finally {
            lock.unlock(); // Ensure the lock is released
        }
    }

    private void refillTokens() {
        long currentTime = System.nanoTime();
        long timeSinceLastRefill = currentTime - lastRefillTime;

        // Calculate the number of tokens to add
        int tokensToAdd = (int) (timeSinceLastRefill / 1_000_000_000L * refillRate);

        if (tokensToAdd > 0) {
            int newTokenCount = Math.min(currentTokens.get() + tokensToAdd, maxTokens);
            currentTokens.set(newTokenCount);
            lastRefillTime = currentTime; // Update the last refill time
        }
    }
}
