package com.cryptfolio.exchange_service.ApplicationConfigs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class CacheConfig {

    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("quote", "coins");
    }

    @Scheduled(fixedRate = 60000)
    public void clearCache() {
        CacheManager cacheManager = cacheManager();
        try {
            cacheManager().getCache("quote").clear();
        } catch (Exception e) {
            logger.error("Error clearing cache: ", e);
        }

    }
}
