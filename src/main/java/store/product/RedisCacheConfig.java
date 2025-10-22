package store.product;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.Map;

@Configuration
public class RedisCacheConfig {

  @Bean
  public RedisCacheManager cacheManager(RedisConnectionFactory cf) {
    var defaults = RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofMinutes(5));

    var perCache = Map.<String, RedisCacheConfiguration>of(
        "product-by-id", defaults.entryTtl(Duration.ofMinutes(10)),
        "products-list", defaults.entryTtl(Duration.ofMinutes(2))
    );

    return RedisCacheManager.builder(cf)
        .cacheDefaults(defaults)
        .withInitialCacheConfigurations(perCache)
        .build();
  }
}
