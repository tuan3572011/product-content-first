package com.product.content.security.service;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Service
public class ForceLogoutCacheService {
    private static final String CACHE_NAME = "forceInvalidToken";
    private static CacheManager cacheManager = null;

    @PostConstruct
    private void init(){
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache(CACHE_NAME,CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(String.class, String.class,
                                ResourcePoolsBuilder.newResourcePoolsBuilder()
                                        .heap(100, MemoryUnit.MB))
                        .withExpiry(Expirations.timeToLiveExpiration(Duration.of(60,
                                TimeUnit.MINUTES))).build())
                .build(true);
    }

    @PreDestroy
    private void destroy(){
        if(!Objects.isNull(cacheManager)){
            cacheManager.close();
        }
    }


    private Cache<String, String> getForceInvalidTokenCache(){
        return cacheManager.getCache(CACHE_NAME, String.class, String.class);
    }

    public boolean contains(String token){
        return getForceInvalidTokenCache().get(token) != null;
    }

    public void put(String token){
        getForceInvalidTokenCache().putIfAbsent(token,"");
    }
}
