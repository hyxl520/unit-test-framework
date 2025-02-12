package io.github.hyxl520.annotations;

import io.github.hyxl520.RedisServerConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 是否启用mockRedis服务
 *
 * @author Jingge
 * @date 2024-12-09 15:14
 * @email 1158055613@qq.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RedisServerConfig.class})
public @interface EnableMockRedisServer {
	/**
	 * 端口号，默认是16379
	 */
	int port() default 16379;

	/**
	 * 分配的内存空间，单位是MB
	 */
	int mallocMemorySize() default 128;
}
