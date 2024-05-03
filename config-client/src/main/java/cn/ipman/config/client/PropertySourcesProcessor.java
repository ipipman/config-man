package cn.ipman.config.client;

import lombok.Data;
import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * ipman property sources processor
 *
 * @Author IpMan
 * @Date 2024/5/3 22:51
 */
@Data
public class PropertySourcesProcessor implements BeanFactoryPostProcessor, EnvironmentAware, PriorityOrdered {

    private final static String IPMAN_PROPERTY_SOURCES = "IMPropertySources";
    private final static String IPMAN_PROPERTY_SOURCE = "IMPropertySource";

    Environment environment;

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ConfigurableEnvironment env = (ConfigurableEnvironment) environment;
        if (env.getPropertySources().contains(IPMAN_PROPERTY_SOURCES)) {
            return;
        }
        // 通过http请求,去ipman-config-server 获取配置
        Map<String, String> config = new HashMap<>();
        config.put("ipman.a", "aa500");
        config.put("ipman.b", "bb600");
        config.put("ipman.c", "cc700");

        IMConfigService configService = new IMConfigServiceImpl(config);
        IMPropertySource propertySource = new IMPropertySource(IPMAN_PROPERTY_SOURCE, configService);
        // 组合的属性源
        CompositePropertySource composite = new CompositePropertySource(IPMAN_PROPERTY_SOURCES);
        composite.addPropertySource(propertySource);
        // 添加到环境变量最前面
        env.getPropertySources().addFirst(composite);

    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }


    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }
}
