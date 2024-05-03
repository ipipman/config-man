package cn.ipman.config.client.config;

import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.lang.Nullable;

/**
 * Description for this class
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
