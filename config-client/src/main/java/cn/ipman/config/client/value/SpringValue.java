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

    private Object bean;
    private String beanName;
    private String key;
    private String placeholder;
    private Field field;

}
