package cn.ipman.config.client.config;

import cn.ipman.config.client.config.IMConfigService;
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

    /**
     * 构造函数，初始化属性源。
     * 通过SpringPropertySource添加配置中心数据源, 这样Spring就能拿到我们写入的配置了
     *
     * @param name 属性源的名称。
     * @param source 提供配置属性的服务实例。
     */
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
