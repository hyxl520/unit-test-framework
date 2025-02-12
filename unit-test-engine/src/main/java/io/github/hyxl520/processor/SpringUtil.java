package io.github.hyxl520.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Spring的动态Bean获取工具类
 *
 * @author Jingge
 * @date 2021/12/16 16:25
 */
@Component("SpringPlusContainer")
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SpringUtil implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.applicationContext = applicationContext;
    }

    /**
     * 获取Spring上下文环境的参数，默认是null
     *
     * @param key 键
     * @return java.lang.String
     * @author Jingge(* ^ ▽ ^ *)
     * @date 2024/5/9 15:24
     */
    public static String getProperty(String key) {
        return getProperty(key, null);
    }

    public static String getProperty(String key,
                                     String defaultValue) {
        if (applicationContext == null) {
            return defaultValue;
        }
        try {
            String value = applicationContext.getEnvironment().getProperty(key);
            return StringUtils.isEmpty(value) ? defaultValue : value;
        } catch (Exception ignored) {
        }
        return defaultValue;
    }

    //通过name获取 Bean.
    public static Object getBean(String name) {
        if (applicationContext == null) {
            return null;
        }
        try {
            return applicationContext.getBean(name);
        } catch (Exception ignored) {
        }
        return null;
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext == null) {
            return null;
        }
        try {
            return applicationContext.getBean(clazz);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static <T> T getNonnullBean(Class<T> clazz) {
        T bean = getBean(clazz);
        if (bean == null) {
            throw new RuntimeException("bean 未找到");
        }
        return bean;
    }

    public static <T> T getNonnullBean(String name,
                                       Class<T> clazz) {
        T bean = getBean(name, clazz);
        if (bean == null) {
            throw new RuntimeException("bean 未找到");
        }
        return bean;
    }

    public static <T> Optional<T> getBeanOptional(Class<T> clazz) {
        if (applicationContext == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(applicationContext.getBean(clazz));
        } catch (Exception ignored) {
        }
        return Optional.empty();
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name,
                                Class<T> clazz) {
        if (applicationContext == null) {
            return null;
        }
        try {
            return applicationContext.getBean(name, clazz);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static <T> Set<T> getBeanOfSubType(Class<T> subType) {
        if (applicationContext == null) {
            return Collections.emptySet();
        }
        Map<String, T> holder = applicationContext.getBeansOfType(subType);
        if (CollectionUtils.isEmpty(holder)) {
            return Collections.emptySet();
        }
        return new HashSet<>(holder.values());
    }

    public static <T> T getInstance(Class<T> clazz) {
        T instance = getBean(clazz);
        return instance == null ? getClassInstance(clazz) : instance;
    }

    public static <T> T getInstance(String name,
                                    Class<T> clazz) {
        T instance = getBean(name, clazz);
        return instance == null ? getClassInstance(clazz) : instance;
    }

    public static <T> T getClassInstance(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }
        try {
            Constructor<T> constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            log.warn("获取类实例失败", e);
        }
        return null;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        SpringUtil.applicationContext = event.getApplicationContext();
    }
}
