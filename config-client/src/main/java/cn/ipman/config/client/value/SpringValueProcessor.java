package cn.ipman.config.client.value;

import cn.ipman.config.client.repository.IMRepositoryChangeListener;
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
import org.springframework.context.event.EventListener;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    static final PlaceholderHelper placeholderHelper = PlaceholderHelper.getInstance();
    static final MultiValueMap<String, SpringValue> VALUE_HOLDER = new LinkedMultiValueMap<>();
    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(@NotNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

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

    @Override
    public void onApplicationEvent(@NotNull EnvironmentChangeEvent event) {
        event.getKeys().forEach(key -> {
            log.info("[IM_CONFIG] >> update spring value: {}", key);
            List<SpringValue> springValues = VALUE_HOLDER.get(key);
            if (springValues == null | Objects.requireNonNull(springValues).isEmpty()) {
                return;
            }

            springValues.forEach(springValue -> {
                log.info("[IM_CONFIG] >> update spring value:{} for key:{}", springValue, key);
                try {
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
