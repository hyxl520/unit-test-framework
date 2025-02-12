package io.github.hyxl520.processor;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * BeanDefinitionRegistry的对外门面，用于定义移除的条件，该类省去了直接对Bean的操作
 *
 * @author Jingge
 * @date 2025-02-11 10:47
 * @email 1158055613@qq.com
 */
public class BeanDefinitionRegistryFacade {
	private final BeanDefinitionRegistry registry;

	public BeanDefinitionRegistryFacade(BeanDefinitionRegistry registry) {
		this.registry = registry;
	}

	public boolean containsBeanDefinition(String beanName) {
		return registry.containsBeanDefinition(beanName);
	}

	public BeanDefinition getBeanDefinition(String beanName) {
		return registry.getBeanDefinition(beanName);
	}

	public String[] getBeanDefinitionNames() {
		return registry.getBeanDefinitionNames();
	}

	public int getBeanDefinitionCount() {
		return registry.getBeanDefinitionCount();
	}

	public boolean isBeanDefinitionOverridable(String beanName) {
		return registry.isBeanDefinitionOverridable(beanName);
	}

	public boolean isBeanNameInUse(String beanName) {
		return registry.isBeanNameInUse(beanName);
	}

}
