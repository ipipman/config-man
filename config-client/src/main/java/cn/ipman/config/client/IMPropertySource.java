package cn.ipman.config.client;

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
    @Nullable
    public String[] getPropertyNames() {
        if (source.getPropertyNames().length == 0){
            return new String[]{};
        }
        return source.getPropertyNames();
    }

    @Override
    @Nullable
    public Object getProperty(@Nullable String name) {
        return source.getProperty(name);
    }
}
