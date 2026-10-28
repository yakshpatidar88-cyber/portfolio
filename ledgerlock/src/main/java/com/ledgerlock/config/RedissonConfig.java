package com.ledgerlock.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * =====================================================================================
 * Configuration: RedissonConfig
 * Initializes the Redisson distributed lock client and connection pool.
 * =====================================================================================
 */
@Configuration
public class RedissonConfig {

    private static final Logger log = LoggerFactory.getLogger(RedissonConfig.class);

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    /**
     * Creates the RedissonClient bean configured for high-concurrency distributed locking.
     * 
     * Key Parameters:
     * - ConnectionPoolSize: 32 concurrent connections for lock acquisition
     * - MinimumIdleSize: 8 keep-alive connections
     * - IdleConnectionTimeout: 10000ms
     * - ConnectTimeout: 5000ms
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        String redisAddress = String.format("redis://%s:%d", redisHost, redisPort);
        log.info("Configuring Redisson Distributed Lock Manager connecting to: {}", redisAddress);

        Config config = new Config();
        config.useSingleServer()
                .setAddress(redisAddress)
                .setConnectionPoolSize(32)
                .setConnectionMinimumIdleSize(8)
                .setConnectTimeout(5000)
                .setTimeout(3000)
                .setRetryAttempts(3)
                .setRetryInterval(1500);

        return Redisson.create(config);
    }
}
