package cn.ipman.config.client.registry;

import cn.ipman.config.client.meta.ConfigMeta;
import cn.ipman.config.client.config.IMConfigService;
import cn.ipman.config.client.config.IMPropertySource;
import lombok.Data;
import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;


/**
 * ipman property sources processor
 * 该类是一个配置类，用于在Spring应用启动时，通过http请求从ipman-config-server获取配置，并将配置添加到Spring环境变量中。
 *
 * @Author IpMan
 * @Date 2024/5/3 22:51
 */
@Data
public class PropertySourcesProcessor implements BeanFactoryPostProcessor, ApplicationContextAware, EnvironmentAware, PriorityOrdered {

    private final static String IPMAN_PROPERTY_SOURCES = "IMPropertySources";
    private final static String IPMAN_PROPERTY_SOURCE = "IMPropertySource";

    Environment environment;
    ApplicationContext applicationContext;

    /**
     * 处理 BeanFactory，在 Spring 应用启动过程中注入自定义属性源。
     *
     * @param beanFactory ConfigurableListableBeanFactory，
     *                    Spring BeanFactory 的一个接口，提供访问和操作 Spring 容器中所有 Bean 的能力。
     * @throws BeansException 如果处理过程中发生错误。
     */
    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 检查是否已存在 ipman 的属性源，若存在则不重复添加
        ConfigurableEnvironment ENV = (ConfigurableEnvironment) environment;
        if (ENV.getPropertySources().contains(IPMAN_PROPERTY_SOURCES)) {
            return;
        }

        // 设置config-server远程服务的调用信息
        String app = ENV.getProperty("ipman.app", "app1");
        String env = ENV.getProperty("ipman.env", "dev");
        String ns = ENV.getProperty("ipman.ns", "public");
        String configServer = ENV.getProperty("ipman.configServer", "http://localhost:9129");

        // 使用获取到的配置创建配置服务和属性源
        ConfigMeta configMeta = new ConfigMeta(app, env, ns, configServer);

        // 创建配置中心实现类, 省去技术细节, 理解了下:
        // 1.启动时候 ConfigService 从 Repository拿配置,  同时 Repository 关联了 ConfigService 这个对象,.
        // 2.当 Repository 巡检发现配置变了, 在去改 ConfigService 里的 config.
        // 3.改完后, 最终再用EnvironmentChangeEvent 去刷新
        IMConfigService configService = IMConfigService.getDefault(applicationContext, configMeta);

        // 创建SpringPropertySource, 此时Spring就能识别我们自定义的配置了
        IMPropertySource propertySource = new IMPropertySource(IPMAN_PROPERTY_SOURCE, configService);

        // 创建组合属性源并将 ipman 的属性源添加到其中
        CompositePropertySource composite = new CompositePropertySource(IPMAN_PROPERTY_SOURCES);
        composite.addPropertySource(propertySource);

        // 将组合属性源添加到环境变量中，并确保其被最先访问
        ENV.getPropertySources().addFirst(composite);

    }

    /**
     * 获取Bean处理器的优先级，实现 PriorityOrdered 接口。
     *
     * @return int 返回处理器的优先级，值越小优先级越高。
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * 设置 Spring 环境配置
     *
     * @param environment Environment，Spring 环境接口，提供环境变量的访问。
     */
    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    /**
     * 设置应用上下文
     *
     * @param applicationContext Spring应用的上下文环境。
     */
    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
