package io.github.hyxl520.annotations;

import io.github.hyxl520.DatasourceConfig;
import io.github.hyxl520.JingGeExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启Mock database，开启后会根据配置的初始化脚本来对h2database进行初始化
 *
 * @author Jingge
 * @date 2024-12-12 10:30
 * @email 1158055613@qq.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ExtendWith({JingGeExtension.class})
@Import(DatasourceConfig.class)
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public @interface EnableMockDatabase {
	/**
	 * 初始化h2database表结构的SQL脚本路径，该脚本会在每个单测类执行之前执行，该脚本必须放到类路径下
	 */
	String[] initSchemaSqlLocations() default "";

	/**
	 * 初始化h2database表数据的SQL脚本路径，该脚本会在每个单测类执行之前执行，该脚本必须放到类路径下
	 */
	String[] initDataSqlLocations() default "";

	/**
	 * 清除h2database表结构或者表数据的脚本，该脚本会在每个单测类执行完成后执行，为空忽略
	 */
	String[] cleanupSqlLocations() default "";

	/**
	 * 每个单测类执行完成后是否清空所有的表和数据
	 */
	boolean cleanupAllTablesAndData() default true;
}
