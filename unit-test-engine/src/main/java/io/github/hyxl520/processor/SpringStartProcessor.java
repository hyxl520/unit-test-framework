package io.github.hyxl520.processor;

/**
 * Spring启动处理器
 *
 * @author Jingge
 * @date 2022/09/19 9:45
 */
public interface SpringStartProcessor extends SpringProcessor {
    void onStart();
}
