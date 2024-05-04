package cn.ipman.config.client.repository;

import cn.ipman.config.client.config.ConfigMeta;

import java.util.Map;

/**
 * interface to get config
 *
 * @Author IpMan
 * @Date 2024/5/4 19:11
 */
public interface IMRepository {

    static IMRepository getDefault(ConfigMeta meta) {
        return new IMRepositoryImpl(meta);
    }

    Map<String, String> getConfig();
}
