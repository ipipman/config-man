package cn.ipman.config.client.config;

import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.lang.Nullable;

/**
 * Description for this class
 * 该类是EnumerablePropertySource的子类，用于提供配置属性。
 * 它将IMConfigService作为属性源，
 *    - 可以通过getPropertyNames()获取所有属性名，
 *    - 通过getProperty(String name)获取指定属性的值。
 *
 * @Author IpMan
 * @Date 2024/5/3 22:26
 */
public class IMPropertySource extends EnumerablePropertySource<IMConfigService> {

    public IMPropertySource(String name, IMConfigService source) {
        super(name, source);
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public String[] getPropertyNames() {
        return source.getPropertyNames();
    }

    @Override
    public Object getProperty(@Nullable String name) {
        return source.getProperty(name);
    }
}
