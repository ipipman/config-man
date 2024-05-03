package cn.ipman.config.client.config;

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
 *
 * @Author IpMan
 * @Date 2024/5/3 23:11
 */
public class IMConfigRegistry implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
                                        @NonNull BeanDefinitionRegistry registry) {

        // ImportBeanDefinitionRegistrar.super.registerBeanDefinitions(importingClassMetadata, registry);

        // 判断PropertySourcesProcessor 是否已经注册Bean
        Optional<String> first = Arrays.stream(registry.getBeanDefinitionNames())
                .filter(x -> PropertySourcesProcessor.class.getName().equals(x)).findFirst();
        if (first.isPresent()) {
            System.out.println("PropertySourcesProcessor already registered");
            return;
        }

        // 注册PropertySourcesProcessor
        System.out.println("register PropertySourcesProcessor");
        AbstractBeanDefinition beanDefinition =
                BeanDefinitionBuilder.genericBeanDefinition(PropertySourcesProcessor.class).getBeanDefinition();
        registry.registerBeanDefinition(PropertySourcesProcessor.class.getName(), beanDefinition);

    }
}
