//package com.lld.im.service.config;
//
//import com.fasterxml.jackson.annotation.JsonAutoDetect;
//import com.fasterxml.jackson.annotation.PropertyAccessor;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//
//@Configuration
//@RequiredArgsConstructor
//public class RedisConfig {
//
//    private final RedisConnectionFactory redisConnectionFactory;
//
//    @Bean
//    public RedisTemplate<Object, Object> redisTemplate() {
//        RedisTemplate<Object, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(redisConnectionFactory);
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//
//        mapper.activateDefaultTyping(
//                mapper.getPolymorphicTypeValidator(),
//                ObjectMapper.DefaultTyping.NON_FINAL
//        );
//
//        Jackson2JsonRedisSerializer<Object> serializer =
//                new Jackson2JsonRedisSerializer<>(mapper, Object.class);
//
//        StringRedisSerializer stringSerializer = new StringRedisSerializer();
//        template.setKeySerializer(stringSerializer);
//        template.setHashKeySerializer(stringSerializer);
//
//        template.setValueSerializer(serializer);
//        template.setHashValueSerializer(serializer);
//
//        template.afterPropertiesSet();
//        return template;
//    }
//}