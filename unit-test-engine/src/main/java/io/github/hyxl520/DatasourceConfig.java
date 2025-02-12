package io.github.hyxl520;

import io.github.hyxl520.annotations.EnableMockDatabase;
import io.github.hyxl520.processor.MockDatabaseType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * @author Jingge
 * @date 2025-01-15 16:22
 * @email 1158055613@qq.com
 */
@Slf4j
public class DatasourceConfig implements ImportAware {
	public static DataSource GLOBAL_TEST_DATASOURCE;

	private static MockDatabaseType mockDatabaseType;

	public DataSource generateDatasource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		String mode = mockDatabaseType == null ? "MySQL" : mockDatabaseType.getMode();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUrl(String.format(
			"jdbc:h2:mem:demo;MODE=%s;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=0;DB_CLOSE_ON_EXIT=FALSE", mode));
		return dataSource;
	}

	@Bean(name = "dataSource")
	@Primary
	public DataSource dataSource() {
		String mode = mockDatabaseType == null ? "MySQL" : mockDatabaseType.getMode();
		log.info("the h2database mock mode is {}", mode);
		return generateDatasource();
	}

	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {
		DatasourceConfig.mockDatabaseType = (MockDatabaseType)Objects.requireNonNull(
			                                                             AnnotationAttributes.fromMap(importMetadata.getAnnotationAttributes(EnableMockDatabase.class.getName())))
		                                                             .get("databaseType");
		DatasourceConfig.GLOBAL_TEST_DATASOURCE = generateDatasource();
	}
}
