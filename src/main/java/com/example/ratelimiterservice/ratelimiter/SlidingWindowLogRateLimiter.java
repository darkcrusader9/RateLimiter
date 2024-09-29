package com.example.ratelimiterservice.ratelimiter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantLock;

public class SlidingWindowLogRateLimiter implements RateLimiter {
    private final long windowSizeMillis;
    private final int maxRequests;
    private final ConcurrentHashMap<String, ConcurrentLinkedDeque<Long>> userVsTimeStampLog;
    private final ReentrantLock lock = new ReentrantLock();

    public SlidingWindowLogRateLimiter(long windowSizeMillis, int maxRequests) {
        this.windowSizeMillis = windowSizeMillis;
        this.maxRequests = maxRequests;
        this.userVsTimeStampLog = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allowRequest(String userId) {
        // Get or create the timestamp log for the user
        ConcurrentLinkedDeque<Long> timeStampLog = userVsTimeStampLog.computeIfAbsent(userId, k -> new ConcurrentLinkedDeque<>());

        lock.lock(); // Lock only around critical section
        try {
            long currentTime = System.currentTimeMillis();

            // Remove timestamps outside the window
            while (!timeStampLog.isEmpty() && (currentTime - timeStampLog.peekFirst()) > windowSizeMillis) {
                timeStampLog.pollFirst();
            }

            // Check if the request can be allowed
            if (timeStampLog.size() < maxRequests) {
                timeStampLog.addLast(currentTime); // Log the current timestamp
                return true; // Allow the request
            }

            return false; // Deny the request
        } finally {
            lock.unlock(); // Always unlock
        }
    }
}
