package io.github.hyxl520.annotations;

import io.github.hyxl520.JingGeExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

/**
 * 单测的配置注解，其是一个组合注解，提供JingGe unit test frame work的常规配置
 *
 * @author Jingge
 * @date 2024-12-09 15:30
 * @email 1158055613@qq.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ComponentScan(basePackages = "io.github.hyxl520")
@ActiveProfiles("unittest")
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAutoConfiguration
@ExtendWith(JingGeExtension.class)
public @interface JingGeUnitTest {
}
