package cn.ipman.config.client;

import java.util.Map;

/**
 * ipman config service impl
 *
 * @Author IpMan
 * @Date 2024/5/3 22:47
 */
public class IMConfigServiceImpl implements IMConfigService {

    Map<String, String> config;

    public IMConfigServiceImpl(Map<String, String> config) {
        this.config = config;
    }

    @Override
    public String[] getPropertyNames() {
        if (this.config == null) {
            return new String[0];
        }
        return this.config.keySet().toArray(new String[0]);
    }

    @Override
    public String getProperty(String name) {
        return this.config.getOrDefault(name, null);
    }
}

