package io.github.hyxl520.processor;

/**
 * 标明一个类为Spring关闭处理器
 *
 * @author Jingge
 * @date 2022/09/19 9:46
 */
public interface SpringEndProcessor extends SpringProcessor {
    void onEnd();
}
