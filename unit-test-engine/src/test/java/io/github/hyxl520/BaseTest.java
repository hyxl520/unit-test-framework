package io.github.hyxl520;

import io.github.hyxl520.annotations.EnableMockDatabase;
import io.github.hyxl520.annotations.EnableMockRedisServer;
import io.github.hyxl520.annotations.EnableUselessBeanAutoRemoved;
import io.github.hyxl520.annotations.JingGeUnitTest;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Yongxiang
 * @date 2025-01-22 16:26
 * @email 1158055613@qq.com
 */
@SpringBootTest(classes = DefaultTestBootApplication.class)
@JingGeUnitTest
@EnableUselessBeanAutoRemoved
@EnableMockRedisServer
public class BaseTest {
}
