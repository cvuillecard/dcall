package com.dcall.core.configuration.app.spring;

import com.dcall.core.configuration.utils.ResourceUtils;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

//@Configuration
//@EnableCaching
//@ComponentScans(value = { @ComponentScan({ "com.dcall.core" }) })
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
