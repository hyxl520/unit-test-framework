package io.github.hyxl520;

import io.github.hyxl520.annotations.JingGeUnitTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;

/**
 * 默认的测试套件启动类
 *
 * @author Jingge
 * @date 2024-12-09 11:21
 * @email 1158055613@qq.com
 */
@JingGeUnitTest
@Slf4j
public class DefaultTestBootApplication {
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(DefaultTestBootApplication.class);
		application.run(args);
		log.info(
			"==================================>JingGe unit test framework default boot application has been started.");
	}
}
