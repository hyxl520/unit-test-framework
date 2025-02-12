package io.github.hyxl520;

import lombok.extern.slf4j.Slf4j;
import redis.embedded.RedisServer;
import redis.embedded.core.RedisServerBuilder;

import java.io.IOException;

/**
 * @author Jingge
 * @date 2025-02-08 16:42
 * @email 1158055613@qq.com
 */
@Slf4j
public class RedisServerManager {
	private static final String LOCALHOST = "127.0.0.1";
	public static RedisServer redisServer;

	public static void startServer(int size,
	                               int port) {
		if (isRedisServerRunning()) {
			System.out.println("redis server has already started");
		}
		System.out.println(("JingGeUnitTestFramework=>mock redis server starting"));
		try {
			redisServer = new RedisServerBuilder().setting(String.format("maxmemory %dm", size)).port(port)
			                                      .setting("bind " + LOCALHOST).build();
			redisServer.start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.out.printf("JingGeUnitTestFramework=>mock redis server started on port %d with %dMB memory size\n", port,
		                  size);
	}

	public static void closeServer() {
		if (redisServer != null && redisServer.isActive()) {
			System.out.println("JingGeUnitTestFramework=>mock redis server ending");
			try {
				redisServer.stop();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			System.out.println("JingGeUnitTestFramework=>mock redis server ended");
		}
	}

	public static boolean isRedisServerRunning() {
		return redisServer != null && redisServer.isActive();
	}

}
