package com.dcall.core.configuration.spring;

import com.dcall.core.configuration.constant.ConstantResource;
import com.dcall.core.configuration.exception.TechnicalException;
import com.dcall.core.configuration.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@Configuration
@EnableCaching
@ComponentScans(value = { @ComponentScan({ "com.dcall.core" }) })
public class RedisConfig {

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        return new JedisConnectionFactory(
                new RedisStandaloneConfiguration(
                        ResourceUtils.getString("redis.local.host"), ResourceUtils.getInt("redis.local.port")
                )
        );
    }

    @Bean
    public CacheManager redisCacheManager() {
        return RedisCacheManager.create(redisConnectionFactory());
    }
}
