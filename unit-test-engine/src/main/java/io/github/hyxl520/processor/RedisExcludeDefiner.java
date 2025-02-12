package io.github.hyxl520.processor;

import io.github.hyxl520.RedisServerConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.convert.RedisConverter;

import java.util.List;

/**
 * 排除Redis相关依赖，当没有使用{@link io.github.hyxl520.annotations.EnableMockRedisServer}时生效
 *
 * @author Jingge
 * @date 2025-02-11 11:04
 * @email 1158055613@qq.com
 */
public class RedisExcludeDefiner implements BeanExcludeDefiner {
	@Override
	public List<Class<?>> excludeBeansByClass() {
		return List.of(RedisProperties.class, RedisAutoConfiguration.class, RedisTemplate.class,
		               StringRedisTemplate.class, RedisConverter.class, RedisKeyValueAdapter.class,
		               RedisKeyValueTemplate.class);
	}

	@Override
	public List<String> excludeBeansByName() {
		return List.of("redisReferenceResolver");
	}

	@Override
	public boolean conditionOn(BeanDefinitionRegistryFacade beanDefinitionRegistryFacade) {
		return !beanDefinitionRegistryFacade.containsBeanDefinition(RedisServerConfig.class.getName());
	}
}
