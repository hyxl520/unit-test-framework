package io.github.hyxl520.annotations;

import io.github.hyxl520.processor.BeanExcludeDefiner;
import io.github.hyxl520.processor.MockBeanAutoExcludeProcessor;
import io.github.hyxl520.processor.RedisExcludeDefiner;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 是否自动移除不需要的Bean，当单测类未加某些注解或开启某些配置时，将会移除一些不需要的AutoConfiguration
 *
 * @author Jingge
 * @date 2024-12-19 15:17
 * @email 1158055613@qq.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MockBeanAutoExcludeProcessor.Registrar.class)
public @interface EnableUselessBeanAutoRemoved {
	Class<? extends BeanExcludeDefiner>[] definers() default RedisExcludeDefiner.class;
}
