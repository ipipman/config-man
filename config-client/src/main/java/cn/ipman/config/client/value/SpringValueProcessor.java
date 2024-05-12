package cn.ipman.config.client.value;

import cn.ipman.config.client.repository.IMRepositoryChangeListener;
import cn.ipman.config.client.utils.FieldUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;

import java.lang.reflect.Field;
import java.util.List;

/**
 * process spring value
 * 1. 扫描所有 spring value,保存起来
 * 2. 在配置变更时, 更新所有 spring value
 *
 * @Author IpMan
 * @Date 2024/5/12 12:04
 */
public class SpringValueProcessor implements BeanPostProcessor, ApplicationListener<EnvironmentChangeEvent> {

    @Override
    public Object postProcessBeforeInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        List<Field> fields = FieldUtils.findAnnotatedField(bean.getClass(), Value.class);
        fields.forEach(
                field -> {
                    Value value = field.getAnnotation(Value.class);
                    value.value();
                }
        );
        return bean;
    }


    @Override
    public void onApplicationEvent(@NotNull EnvironmentChangeEvent event) {

    }
}
