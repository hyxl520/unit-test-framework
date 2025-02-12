package io.github.hyxl520;

import io.github.hyxl520.annotations.EnableMockRedisServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Objects;

/**
 * @author Jingge
 * @date 2024-12-09 15:15
 * @email 1158055613@qq.com
 */
@Slf4j
public class RedisServerConfig implements ImportAware {
	private static final String LOCALHOST = "127.0.0.1";
	private int port = 16379;

	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {
		port = (int)Objects.requireNonNull(
			                   AnnotationAttributes.fromMap(importMetadata.getAnnotationAttributes(EnableMockRedisServer.class.getName())))
		                   .get("port");
	}

	@Bean
	@Primary
	public RedisProperties redisProperties() {
		RedisProperties redisProperties = new RedisProperties();
		redisProperties.setHost(LOCALHOST);
		redisProperties.setPort(port);
		return redisProperties;
	}
}
