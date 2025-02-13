package io.github.hyxl520;

import io.github.hyxl520.annotations.EnableMockDatabase;
import io.github.hyxl520.annotations.MockData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.DirtiesContext;

/**
 * @author Yongxiang
 * @date 2024-12-09 10:58
 * @email 1158055613@qq.com
 */
@EnableMockDatabase(initSchemaSqlLocations = "/sql/init.sql", initDataSqlLocations = "/sql/data.sql")
public class Test2 extends BaseTest{
	@Autowired(required = false)
	private RedisTemplate<String,String> redisTemplate;

	@Test
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
	public void test2() {
		if(redisTemplate!=null) {
			redisTemplate.opsForValue().set("@test1", "1");
			System.out.println(redisTemplate.opsForValue().get("@test1"));
		}else{
			System.out.println("redis template is null");
		}
		System.out.println("hello unit test");
	}
}
