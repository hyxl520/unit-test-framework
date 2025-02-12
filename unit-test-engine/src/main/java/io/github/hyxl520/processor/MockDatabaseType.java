package io.github.hyxl520.processor;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * H2database要模拟的数据库的类型
 *
 * @author Yongxiang
 * @date 2025-02-12 17:01
 * @email 1158055613@qq.com
 */
@Getter
@AllArgsConstructor
public enum MockDatabaseType {
	MY_SQL("mysql", "MySQL"),

	SQL_SERVER("sqlserver", "MSSQLServer"),

	ORACLE("oracle", "Oracle"),

	POSTGRES_SQL("postgresql", "PostgreSQL"),

	DB2("DB2", "DB2"),

	HSQL_DB("HSQLDB", "HSQLDB");
	private final String name;

	private final String mode;
}
