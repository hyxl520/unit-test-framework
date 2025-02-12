package io.github.hyxl520;

import io.github.hyxl520.annotations.EnableMockDatabase;
import io.github.hyxl520.annotations.EnableUselessBeanAutoRemoved;
import io.github.hyxl520.annotations.MockData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author Yongxiang
 * @date 2024-12-09 10:58
 * @email 1158055613@qq.com
 */
public class Test1 extends BaseTest{
	@Autowired(required = false)
	private RedisTemplate<String,String> redisTemplate;

	@Test
	@MockData(initSchemaSqlLocations = "/sql/init.sql", initDataSqlLocations = "/sql/data.sql")
	public void test1() {
		if(redisTemplate!=null) {
			redisTemplate.opsForValue().set("@test1", "1");
			System.out.println(redisTemplate.opsForValue().get("@test1"));
		}else{
			System.out.println("redis template is null");
		}
		System.out.println("hello unit test");
	}

	@Test
	@MockData(initSchemaSqlLocations = "/sql/init.sql", initDataSqlLocations = "/sql/data.sql")
	public void test2() {
		if(redisTemplate!=null) {
			redisTemplate.opsForValue().set("@test1", "1");
			System.out.println(redisTemplate.opsForValue().get("@test1"));
		}else{
			System.out.println("redis template is null");
		}
		System.out.println("hello unit test");
	}

	@Test
	@MockData(initSchemaSqlLocations = "/sql/init.sql", initDataSqlLocations = "/sql/data.sql")
	public void test3() {
		if(redisTemplate!=null) {
			redisTemplate.opsForValue().set("@test1", "1");
			System.out.println(redisTemplate.opsForValue().get("@test1"));
		}else{
			System.out.println("redis template is null");
		}
	}
}
