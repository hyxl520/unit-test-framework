package io.github.hyxl520.processor;

import io.github.hyxl520.annotations.EnableUselessBeanAutoRemoved;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * 自动排除一些不使用的Bean
 *
 * @author Jingge
 * @date 2024-12-19 14:24
 * @email 1158055613@qq.com
 */
@Slf4j
public class MockBeanAutoExcludeProcessor implements BeanDefinitionRegistryPostProcessor {
	private Class<? extends BeanExcludeDefiner>[] excludeDefiners;

	public MockBeanAutoExcludeProcessor() {
	}

	public MockBeanAutoExcludeProcessor(Class<? extends BeanExcludeDefiner>[] excludeDefiners) {
		this.excludeDefiners = excludeDefiners;
	}

	private static String lowerFirstLetter(String str) {
		if (StringUtils.isEmpty(str)) {
			return str;
		}
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		if (excludeDefiners == null) {
			return;
		}
		for (Class<? extends BeanExcludeDefiner> definerClass : excludeDefiners) {
			if (definerClass == null) {
				continue;
			}
			BeanExcludeDefiner definer = getDefinerInstance(definerClass);
			if (definer == null) {
				continue;
			}
			if (definer.conditionOn(new BeanDefinitionRegistryFacade(registry))) {
				List<Object> beans = new ArrayList<>();
				beans.addAll(definer.excludeBeansByClass());
				beans.addAll(definer.excludeBeansByName());
				beans.addAll(definer.excludeBeansByInstance());
				removeBeans(registry, beans);
				if (CollectionUtils.isNotEmpty(definer.excludeBeansByRegex())) {
					String[] beanNames = registry.getBeanDefinitionNames();
					for (String name : beanNames) {
						if (isMatch(definer.excludeBeansByRegex(), registry.getBeanDefinition(name))) {
							removeBeans(registry, List.of(name));
						}
					}
				}
			}
		}
	}

	private boolean isMatch(List<String> regex,
	                        BeanDefinition definition) {
		if (definition == null) {
			return false;
		}
		String className = definition.getBeanClassName();
		if (StringUtils.isEmpty(className)) {
			return false;
		}
		for (String r : regex) {
			if (className.matches(r)) {
				return true;
			}
		}
		return false;
	}

	private void removeBeans(BeanDefinitionRegistry registry,
	                         List<Object> beans) {
		for (Object bean : beans) {
			try {
				if (bean instanceof Class) {
					String beanName = lowerFirstLetter(((Class<?>)bean).getSimpleName());
					registry.removeBeanDefinition(beanName);
					log.info("bean {} will be automatically removed", beanName);
				} else if (bean instanceof String) {
					registry.removeBeanDefinition((String)bean);
					log.info("bean {} will be automatically removed", bean);
				} else {
					String beanName = lowerFirstLetter(bean.getClass().getSimpleName());
					registry.removeBeanDefinition(beanName);
					log.info("bean {} will be automatically removed", beanName);
				}
			} catch (NoSuchBeanDefinitionException ignored) {
			}
		}
	}

	private void registerBeans(BeanDefinitionRegistry registry,
	                           List<Class<?>> beans) {
		for (Class<?> bean : beans) {
			GenericBeanDefinition definition = new GenericBeanDefinition();
			definition.setBeanClass(bean);
			registry.registerBeanDefinition(bean.getSimpleName(), definition);
			log.info("bean {} will be automatically register", bean.getName());
		}
	}

	private BeanExcludeDefiner getDefinerInstance(Class<? extends BeanExcludeDefiner> definerClass) {
		if (definerClass != null) {
			try {
				return definerClass.getConstructor().newInstance();
			} catch (Exception e) {
				log.error("get bean exclude definer {} instance fail.", definerClass.getName(), e);
			}
		}
		return null;
	}

	public static class Registrar implements ImportBeanDefinitionRegistrar {
		@Override
		@SuppressWarnings("unchecked")
		public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
		                                    BeanDefinitionRegistry registry,
		                                    BeanNameGenerator importBeanNameGenerator) {
			AnnotationAttributes attributes = AnnotationAttributes.fromMap(
				importingClassMetadata.getAnnotationAttributes(EnableUselessBeanAutoRemoved.class.getName()));
			if (attributes != null) {
				Class<? extends BeanExcludeDefiner>[] definers =
					(Class<? extends BeanExcludeDefiner>[])attributes.getClassArray("definers");
				MockBeanAutoExcludeProcessor processor = new MockBeanAutoExcludeProcessor(definers);
				registry.registerBeanDefinition("mockBeanAutoExcludeProcessor",
				                                BeanDefinitionBuilder.genericBeanDefinition(
					                                                     MockBeanAutoExcludeProcessor.class, () -> processor)
				                                                     .getBeanDefinition());
			}
		}
	}
}
