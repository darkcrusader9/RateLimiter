package com.example.ratelimiterservice.bucket;

public interface Bucket {
    boolean grant();
}
