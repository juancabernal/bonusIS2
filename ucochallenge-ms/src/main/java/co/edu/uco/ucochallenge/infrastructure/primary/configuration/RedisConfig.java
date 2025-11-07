package co.edu.uco.ucochallenge.infrastructure.primary.configuration;

import java.time.Duration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${uco.cache.users.ttl-seconds:300}")
    private long usersTtlSeconds;

    @Value("${uco.cache.messages.ttl-seconds:900}")
    private long messagesTtlSeconds;

    @Value("${uco.cache.parameters.ttl-seconds:900}")
    private long parametersTtlSeconds;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Host/port vienen de spring.data.redis.*
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(cf);
        GenericJackson2JsonRedisSerializer serializer = redisSerializer();
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory cf) {
        GenericJackson2JsonRedisSerializer serializer = redisSerializer();
        RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        RedisCacheConfiguration usersCfg    = base.entryTtl(Duration.ofSeconds(usersTtlSeconds));
        RedisCacheConfiguration messagesCfg = base.entryTtl(Duration.ofSeconds(messagesTtlSeconds));
        RedisCacheConfiguration paramsCfg   = base.entryTtl(Duration.ofSeconds(parametersTtlSeconds));

        return RedisCacheManager.builder(cf)
                .withCacheConfiguration("usersByPage", usersCfg)
                .withCacheConfiguration("messagesCatalog", messagesCfg)
                .withCacheConfiguration("parametersCatalog", paramsCfg)
                .build();
    }

    private GenericJackson2JsonRedisSerializer redisSerializer() {
        return new GenericJackson2JsonRedisSerializer(redisObjectMapper());
    }

    private ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());
        mapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.EVERYTHING
        );
        return mapper;
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder.createXmlMapper(false).build().findAndRegisterModules();
    }

    /** Clave: users:page={page}:size={size} */
    @Bean(name = "usersPageKeyGenerator")
    public KeyGenerator usersPageKeyGenerator() {
        return (target, method, params) -> "users:page=" + params[0] + ":size=" + params[1];
    }
}
