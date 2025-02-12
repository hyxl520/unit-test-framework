package io.github.hyxl520.processor;

import java.util.Collections;
import java.util.List;

/**
 * 定义单测时需要排除的Bean
 *
 * @author Jingge
 * @date 2025-02-11 10:33
 * @email 1158055613@qq.com
 */
public interface BeanExcludeDefiner {
	/**
	 * 指定要排除的Bean的Class对象
	 *
	 * @return java.util.List<java.lang.Class < ?>>
	 * @author Jingge(* ^ ▽ ^ *)
	 * @date 2025/2/11 10:40
	 */
	default List<Class<?>> excludeBeansByClass() {
		return Collections.emptyList();
	}

	/**
	 * 指定要排除的Bean的名称
	 *
	 * @return java.util.List<java.lang.String>
	 * @author Jingge(* ^ ▽ ^ *)
	 * @date 2025/2/11 10:40
	 */
	default List<String> excludeBeansByName() {
		return Collections.emptyList();
	}

	/**
	 * 指定要排除Bean的实例
	 *
	 * @return java.util.List<java.lang.Object>
	 * @author Jingge(* ^ ▽ ^ *)
	 * @date 2025/2/11 10:41
	 */
	default List<Object> excludeBeansByInstance() {
		return Collections.emptyList();
	}

	/**
	 * 指定要排除Bean的正则，将会使用实际Bean的className来匹配
	 *
	 * @return java.util.List<java.lang.String>
	 * @author Jingge(* ^ ▽ ^ *)
	 * @date 2025/2/11 10:41
	 */
	default List<String> excludeBeansByRegex() {
		return Collections.emptyList();
	}

	/**
	 * 条件匹配时才执行移除策略
	 *
	 * @param beanDefinitionRegistryFacade 门面
	 * @return boolean
	 * @author Jingge(* ^ ▽ ^ *)
	 * @date 2025/2/11 10:53
	 */
	default boolean conditionOn(BeanDefinitionRegistryFacade beanDefinitionRegistryFacade) {
		return true;
	}
}
