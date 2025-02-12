package io.github.hyxl520.annotations;

import io.github.hyxl520.JingGeExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

/**
 * 该注解可用在单个单测方法上，在该单测方法执行之前和之后对Mock的数据源做一些初始化和清理操作，前提是一定有一个单测类使用了@EnableMockDatabase开启了h2database数据源
 *
 * @author Jingge
 * @date 2025-01-17 16:10
 * @email 1158055613@qq.com
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ExtendWith(JingGeExtension.class)
public @interface MockData {
	/**
	 * 初始化h2database表结构的SQL脚本路径，该脚本会在每个单测方法执行之前执行，该脚本必须放到类路径下，为空忽略
	 */
	String[] initSchemaSqlLocations() default "";

	/**
	 * 初始化h2database表数据的SQL脚本路径，该脚本会在每个单测方法执行之前执行，该脚本必须放到类路径下，为空忽略
	 */
	String[] initDataSqlLocations() default "";

	/**
	 * 清除h2database表结构或者表数据的脚本，该脚本会在每个单测方法执行完成后执行，为空忽略
	 */
	String[] cleanupSqlLocations() default "";

	/**
	 * 每个单测方法执行完成后是否清空所有的表和数据
	 */
	boolean cleanupAllTablesAndData() default true;
}
