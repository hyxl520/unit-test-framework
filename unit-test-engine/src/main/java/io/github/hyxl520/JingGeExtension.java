package io.github.hyxl520;

import io.github.hyxl520.annotations.EnableMockDatabase;
import io.github.hyxl520.annotations.EnableMockRedisServer;
import io.github.hyxl520.annotations.MockData;
import io.github.hyxl520.processor.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * JingGe unit test framework的junit单测拓展
 *
 * @author Jingge
 * @date 2024-12-12 10:31
 * @email 1158055613@qq.com
 */
@Slf4j
public class JingGeExtension extends SpringExtension {
	private static final String JING_GE_UNIT_TEST_STORE_KEY = "JingGeUnittest";

	private static final String LOG_PREFIX = "JingGe unit test framework log => ";
	private static final String EXECUTE_TIME_PREFIX = "unitTestExecuteTime";

	private static final String QUERY_TABLE_NAMES_SQL =
		"SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'";

	private static final String QUERY_TABLE_COUNT_SQL =
		"SELECT COUNT(TABLE_NAME) count FROM INFORMATION_SCHEMA.TABLES " + "WHERE TABLE_SCHEMA = 'PUBLIC'";

	private static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS ";

	private void executeInitScripts(String[] initSchemaScripts,
	                                String[] initDataScripts) throws Exception {
		try (Connection connection = getDataSource().getConnection()) {
			if (initSchemaScripts != null) {
				for (String scriptLocation : initSchemaScripts) {
					if (StringUtils.isEmpty(scriptLocation)) {
						continue;
					}
					if (scriptLocation.startsWith("classpath:")) {
						scriptLocation = scriptLocation.replace("classpath:", "");
					}
					log.info("{}ready to execute mock database init schema script {}", LOG_PREFIX, scriptLocation);
					ScriptUtils.executeSqlScript(connection,
					                             new EncodedResource(new ClassPathResource(scriptLocation), "utf8"));
					log.info("{}execute mock database init schema script {} success", LOG_PREFIX, scriptLocation);
				}
			}
			if (initDataScripts != null) {
				for (String scriptLocation : initDataScripts) {
					if (StringUtils.isEmpty(scriptLocation)) {
						continue;
					}
					if (scriptLocation.startsWith("classpath:")) {
						scriptLocation = scriptLocation.replace("classpath:", "");
					}
					log.info("{}ready to execute mock database init data script {}", LOG_PREFIX, scriptLocation);
					ScriptUtils.executeSqlScript(connection,
					                             new EncodedResource(new ClassPathResource(scriptLocation), "utf8"));
					log.info("{}execute mock database init data script {} success", LOG_PREFIX, scriptLocation);
				}
			}
		}
	}

	private void executeCleanUp(String[] cleanupScripts) throws Exception {
		try (Connection connection = getDataSource().getConnection()) {
			if (cleanupScripts != null) {
				for (String scriptLocation : cleanupScripts) {
					if (StringUtils.isEmpty(scriptLocation)) {
						continue;
					}
					if (scriptLocation.startsWith("classpath:")) {
						scriptLocation = scriptLocation.replace("classpath:", "");
					}
					log.info("{}ready to execute mock database cleanup script {}", LOG_PREFIX, scriptLocation);
					ScriptUtils.executeSqlScript(connection,
					                             new EncodedResource(new ClassPathResource(scriptLocation), "utf8"));
					log.info("{}execute mock database cleanup script {} success", LOG_PREFIX, scriptLocation);
				}
			}
		}
	}

	private DataSource getDataSource() {
		DataSource dataSource = SpringUtil.getBean(DataSource.class);
		try {
			if (dataSource == null || dataSource.getConnection().isClosed()) {
				return DatasourceConfig.GLOBAL_TEST_DATASOURCE;
			}
			return dataSource;
		} catch (SQLException e) {
			return DatasourceConfig.GLOBAL_TEST_DATASOURCE;
		}
	}

	private <T extends java.lang.annotation.Annotation> T getAnnotation(ExtensionContext context,
	                                                                    Class<T> annotationClass) {
		if (context.getElement().isEmpty()) {
			return null;
		}
		T annotation = context.getElement().get().getAnnotation(annotationClass);
		if (annotation != null) {
			return annotation;
		} else {
			Optional<Class<?>> testClass = context.getTestClass();
			if (testClass.isPresent()) {
				Class<?> clazz = testClass.get();
				while (clazz != null) {
					annotation = clazz.getDeclaredAnnotation(annotationClass);
					if (annotation != null) {
						return annotation;
					}
					clazz = clazz.getSuperclass();
				}
			}
		}
		return null;
	}

	private EnableMockDatabase getMockDatabaseAnnotation(ExtensionContext context) {
		return getAnnotation(context, EnableMockDatabase.class);
	}

	private EnableMockRedisServer getMockRedisServerAnnotation(ExtensionContext context) {
		return getAnnotation(context, EnableMockRedisServer.class);
	}

	private boolean isWithDirtyContext(ExtensionContext context) {
		return getAnnotation(context, DirtiesContext.class) != null;
	}

	private MockData getMockDataAnnotation(ExtensionContext context) {
		Method testMethod = context.getRequiredTestMethod();
		return testMethod.getAnnotation(MockData.class);
	}

	@Override
	public void beforeAll(ExtensionContext context) throws Exception {
		ExtensionContext.Namespace namespace = ExtensionContext.Namespace.create(JING_GE_UNIT_TEST_STORE_KEY);
		EnableMockRedisServer mockRedisServer = getMockRedisServerAnnotation(context);
		String redisServerKey = "startRedisServer";
		if (mockRedisServer != null && (isWithDirtyContext(context) || context.getRoot().getStore(namespace).get(
			redisServerKey) == null || !RedisServerManager.isRedisServerRunning())) {
			RedisServerManager.startServer(mockRedisServer.mallocMemorySize(), mockRedisServer.port());
			context.getRoot().getStore(namespace).put(redisServerKey, true);
		}
		super.beforeAll(context);
	}

	@Override
	public void postProcessTestInstance(Object testInstance,
	                                    ExtensionContext context) throws Exception {
		super.postProcessTestInstance(testInstance, context);
		ExtensionContext.Namespace namespace = ExtensionContext.Namespace.create(JING_GE_UNIT_TEST_STORE_KEY);
		EnableMockDatabase mockDatabase = getMockDatabaseAnnotation(context);
		String key = testInstance.getClass().getName() + "initScripts";
		if (mockDatabase != null && context.getRoot().getStore(namespace).get(key) == null) {
			executeInitScripts(mockDatabase.initSchemaSqlLocations(), mockDatabase.initDataSqlLocations());
		}
		context.getRoot().getStore(namespace).put(key, true);
	}

	@Override
	public void afterAll(ExtensionContext context) throws Exception {
		EnableMockDatabase mockDatabase = getMockDatabaseAnnotation(context);
		if (mockDatabase != null) {
			if (mockDatabase.cleanupAllTablesAndData()) {
				int total = cleanupAllTables();
				log.info("{} tables and their data have been cleaned up.", total);
				log.info(
					"the cleanupAllTablesAndData is true, so the clean script that defined by EnableMockDatabase annotation won't be executed");
			} else {
				executeCleanUp(mockDatabase.cleanupSqlLocations());
			}
		}
		super.afterAll(context);
		if (isWithDirtyContext(context)) {
			RedisServerManager.closeServer();
		}
	}

	protected int cleanupAllTables() {
		int total = getTotalTableCount();
		try {
			Connection connection = getDataSource().getConnection();
			Statement stmt = connection.createStatement();
			// 查询所有表名
			ResultSet rs = stmt.executeQuery(QUERY_TABLE_NAMES_SQL);
			Map<String, Boolean> dropped = new HashMap<>();
			do {
				if (rs.isClosed() || stmt.isClosed()) {
					connection.close();
					connection = getDataSource().getConnection();
					stmt = connection.createStatement();
					rs = stmt.executeQuery(QUERY_TABLE_NAMES_SQL);
				}
				rs.next();
				String tableName = rs.getString("TABLE_NAME");
				if (dropped.containsKey(tableName)) {
					continue;
				}
				String dropTableSql = DROP_TABLE_SQL + tableName;
				stmt.executeUpdate(dropTableSql);
				dropped.put(tableName, true);
				log.info("Table '{}' has been dropped.", tableName);
			} while (dropped.size() < total);
			rs.close();
			connection.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return total;
	}

	private int getTotalTableCount() {
		try (Connection connection = getDataSource().getConnection()) {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(QUERY_TABLE_COUNT_SQL);
			if (rs.next()) {
				return rs.getInt("count");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return 0;
	}

	private void insertStartTimeMills(String methodName,
	                                  ExtensionContext context) {
		String key = EXECUTE_TIME_PREFIX + "_" + methodName;
		context.getStore(ExtensionContext.Namespace.create(JING_GE_UNIT_TEST_STORE_KEY))
		       .put(key, System.currentTimeMillis());
	}

	private long getStartTimeMills(String methodName,
	                               ExtensionContext context) {
		String key = EXECUTE_TIME_PREFIX + "_" + methodName;
		return (long)context.getStore(ExtensionContext.Namespace.create(JING_GE_UNIT_TEST_STORE_KEY)).get(key);
	}

	@Override
	public void beforeTestExecution(ExtensionContext context) throws Exception {
		super.beforeTestExecution(context);
		Method testMethod = context.getRequiredTestMethod();
		log.info("{}test method {} start execute", LOG_PREFIX, testMethod.getName());
		insertStartTimeMills(testMethod.getName(), context);
		MockData mockData = getMockDataAnnotation(context);
		if (mockData != null) {
			executeInitScripts(mockData.initSchemaSqlLocations(), mockData.initDataSqlLocations());
		}
	}

	@Override
	public void afterTestExecution(ExtensionContext context) throws Exception {
		super.afterTestExecution(context);
		Method testMethod = context.getRequiredTestMethod();
		log.info("{}test method {} finished after {} milliseconds", LOG_PREFIX, testMethod.getName(),
		         System.currentTimeMillis() - getStartTimeMills(testMethod.getName(), context));
		MockData mockData = getMockDataAnnotation(context);
		if (mockData != null) {
			if (mockData.cleanupAllTablesAndData()) {
				int total = cleanupAllTables();
				log.info("{} tables and their data have been cleaned up.", total);
				log.info(
					"the cleanupAllTablesAndData is true, so the clean script that defined by MockData annotation won't be executed");
			} else {
				executeCleanUp(mockData.cleanupSqlLocations());
			}
		}
	}
}
