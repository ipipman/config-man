package cn.ipman.config.client.value;

import cn.ipman.config.client.utils.FieldUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

/**
 * process spring value
 * 1. 扫描所有 spring value,保存起来
 * 2. 在配置变更时, 更新所有 spring value
 *
 * @Author IpMan
 * @Date 2024/5/12 12:04
 */
@Slf4j
public class SpringValueProcessor implements BeanPostProcessor, BeanFactoryAware, ApplicationListener<EnvironmentChangeEvent> {

    // 占位符操作工具,如: ${key:default}, 拿到 key
    static final PlaceholderHelper placeholderHelper = PlaceholderHelper.getInstance();
    // 保存所有使用@SpringValue注解的字段及其相关信息
    static final MultiValueMap<String, SpringValue> VALUE_HOLDER = new LinkedMultiValueMap<>();
    private BeanFactory beanFactory;

    /**
     * 设置BeanFactory，使处理器能够访问Spring BeanFactory。
     *
     * @param beanFactory Spring的BeanFactory。
     * @throws BeansException 如果设置过程中发生错误。
     */
    @Override
    public void setBeanFactory(@NotNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * 在Bean初始化之前处理Bean，扫描并保存所有使用@SpringValue注解的字段。
     *
     * @param bean         当前处理的Bean实例。
     * @param beanName     当前处理的Bean名称。
     * @return 处理后的Bean实例。
     * @throws BeansException 如果处理过程中发生错误。
     */
    @Override
    public Object postProcessBeforeInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        List<Field> fields = FieldUtils.findAnnotatedField(bean.getClass(), Value.class);
        fields.forEach(field -> {
                    log.info("[IM_CONFIG] >> find spring value:{}", field);
                    Value value = field.getAnnotation(Value.class);
                    placeholderHelper.extractPlaceholderKeys(value.value()).forEach(key -> {
                                log.info("[IM_CONFIG] >> find spring value:{} for field:{}", key, field);
                                SpringValue springValue = new SpringValue(bean, beanName, key, value.value(), field);
                                VALUE_HOLDER.add(key, springValue);
                            }
                    );
                }
        );
        return bean;
    }

    /**
     * 当@Value配置, 发生改变时，更新所有相关字段的值。
     *
     * @param event 包含环境变量变更信息的事件。
     */
    @Override
    public void onApplicationEvent(@NotNull EnvironmentChangeEvent event) {
        // 更新所有与变更的键相关的@SpringValue字段的值
        log.info("[IM_CONFIG] >> update spring value for keys: {}", event.getKeys());
        event.getKeys().forEach(key -> {
            log.info("[IM_CONFIG] >> update spring value: {}", key);
            List<SpringValue> springValues = VALUE_HOLDER.get(key);
            if (springValues == null || springValues.isEmpty()) {
                return;
            }

            // 更新每个相关@Value字段的值
            springValues.forEach(springValue -> {
                log.info("[IM_CONFIG] >> update spring value:{} for key:{}", springValue, key);
                try {
                    // 解析并设置新值
                    Object value = placeholderHelper.resolvePropertyValue((ConfigurableBeanFactory) beanFactory,
                            springValue.getBeanName(), springValue.getPlaceholder());
                    log.info("[IM_CONFIG] >> update spring value:{} for holder:{}", value, springValue.getPlaceholder());
                    springValue.getField().setAccessible(true);
                    springValue.getField().set(springValue.getBean(), value);
                } catch (IllegalAccessException ex) {
                    log.error("[IM_CONFIG] >> update spring value error", ex);
                }
            });
        });
    }
}
