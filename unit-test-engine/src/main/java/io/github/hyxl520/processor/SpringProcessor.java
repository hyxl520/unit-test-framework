package io.github.hyxl520.processor;

/**
 * @author Jingge
 * @date 2022/09/19 14:04
 */
public interface SpringProcessor {
    default int getProcessorLevel() {
        return 0;
    }
}
