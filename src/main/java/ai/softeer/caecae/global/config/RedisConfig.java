package ai.softeer.caecae.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${REDIS_PASSWORD}")
    private String redisPassword;

    @Value("${spring.data.redis.readonly-host}")
    private String redisReadOnlyHost;

    @Value("${spring.data.redis.readonly-port}")
    private int redisReadOnlyPort;

    // 레디스 커넥션 생성
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisHost);
        redisConfig.setPort(redisPort);
        redisConfig.setPassword(redisPassword); // 비밀번호 설정

        return new LettuceConnectionFactory(redisConfig);
    }

    // key(String), value(Object)로 구성되는 레디스 템플릿 생성
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer()); // 직렬화 가능하도록 설정
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // 직렬화 가능하도록 설정
        return template;
    }

    // 읽기전용 레디스
    @Bean
    public RedisConnectionFactory redisReadOnlyConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisReadOnlyHost);
        redisConfig.setPort(redisReadOnlyPort);
        redisConfig.setPassword(redisPassword);

        return new LettuceConnectionFactory(redisConfig);
    }

    // 읽기전용 레디스
    @Bean
    public RedisTemplate<String, Object> redisReadOnlyTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisReadOnlyConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }


}