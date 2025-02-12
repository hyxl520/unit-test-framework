package io.github.hyxl520;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * @author Jingge
 * @date 2025-01-15 16:22
 * @email 1158055613@qq.com
 */
public class DatasourceConfig {
	public static DataSource GLOBAL_TEST_DATASOURCE = generateDatasource();

	public static DataSource generateDatasource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUrl(
			"jdbc:h2:mem:demo;MODE=MySQL;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=0;DB_CLOSE_ON_EXIT=FALSE");
		return dataSource;
	}

	@Bean(name = "dataSource")
	@Primary
	public DataSource dataSource() {
		return generateDatasource();
	}
}
