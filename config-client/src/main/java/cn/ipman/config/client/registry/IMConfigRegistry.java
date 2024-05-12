package cn.ipman.config.client.registry;

import cn.ipman.config.client.value.SpringValueProcessor;
import lombok.NonNull;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.Optional;

/**
 * registry ipman config bean
 * 该函数是一个实现ImportBeanDefinitionRegistrar接口的类，用于在Spring容器中注册BeanDefinition。
 * 它的registerBeanDefinitions方法会在导入注解 metadata时被调用，
 * 通过判断PropertySourcesProcessor是否已经注册，来决定是否注册PropertySourcesProcessor。
 * 如果已经注册，则输出"PropertySourcesProcessor already registered"并返回；
 * 如果未注册，则输出"register PropertySourcesProcessor"，并创建PropertySourcesProcessor的BeanDefinition并注册到Spring容器中。
 *
 * @Author IpMan
 * @Date 2024/5/3 23:11
 */
public class IMConfigRegistry implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
                                        @NonNull BeanDefinitionRegistry registry) {

        // 注册 @ConfigurationProperties() 配置方式的注册中心处理器
        registerClass(registry, PropertySourcesProcessor.class);
        // 注册 @Value() 配置方式的注册中心处理器
        registerClass(registry, SpringValueProcessor.class);
    }


    /**
     * 向给定的 BeanDefinitionRegistry 注册一个类。
     * 如果该类已经注册，则不进行重复注册。
     *
     * @param registry BeanDefinitionRegistry 实例，用于注册 Bean。
     * @param aClass   需要注册的类。
     */
    private static void registerClass(BeanDefinitionRegistry registry, Class<?> aClass) {
        System.out.println("registry " + aClass.getName());
        // 判断PropertySourcesProcessor 是否已经注册Bean
        Optional<String> first = Arrays.stream(registry.getBeanDefinitionNames())
                .filter(x -> aClass.getName().equals(x))
                .findFirst();
        if (first.isPresent()) {
            System.out.println(aClass.getName() + " already registered");
            return;
        }
        // 注册PropertySourcesProcessor
        AbstractBeanDefinition beanDefinition =
                BeanDefinitionBuilder.genericBeanDefinition(aClass).getBeanDefinition();
        registry.registerBeanDefinition(aClass.getName(), beanDefinition);

        System.out.println("registered " + aClass.getName());
    }
}
