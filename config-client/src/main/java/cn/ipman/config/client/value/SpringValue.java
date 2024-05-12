package cn.ipman.config.client.value;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Field;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/5/12 13:05
 */
@Data
@AllArgsConstructor
public class SpringValue {

    private Object bean;           // 配置关联的关联的 Bean 对象
    private String beanName;       // 配置关联的关联的 Bean 对象名称
    private String key;            // @Value配置的key
    private String placeholder;    // @Value配置的占位符
    private Field field;           // @Value配置的 Bean 成员

}
