package io.github.hyxl520.processor;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Comparator;

/**
 * Spring 启动关闭处理器
 *
 * @author Jingge
 * @date 2022/09/19 9:47
 */
@Component("defaultSpringProcessor")
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class SpringStartAndEndProcessor implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        for (SpringStartProcessor processor : SpringUtil.getBeanOfSubType(SpringStartProcessor.class).stream()
                                                        .sorted(new ProcessorComparator()).toList()) {
            try {
                processor.onStart();
            } catch (Exception e) {
                log.error("启动处理器{}执行时发生异常：{}", processor.getClass().getName(), e.getMessage(), e);
            }
        }
    }

    @PreDestroy
    public void destroy() {
        for (SpringEndProcessor processor : SpringUtil.getBeanOfSubType(SpringEndProcessor.class).stream()
                                                      .sorted(new ProcessorComparator()).toList()) {
            try {
                processor.onEnd();
            } catch (Exception e) {
                log.error("结束处理器{}执行时发生异常：{}", processor.getClass().getName(), e.getMessage(), e);
            }
        }
    }

    private static class ProcessorComparator implements Comparator<SpringProcessor> {
        @Override
        public int compare(SpringProcessor o1,
                           SpringProcessor o2) {
            return Integer.compare(o2.getProcessorLevel(), o1.getProcessorLevel());
        }
    }
}
