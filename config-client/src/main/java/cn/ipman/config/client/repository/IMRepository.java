package cn.ipman.config.client.repository;

import cn.ipman.config.client.meta.ConfigMeta;

import java.util.Map;

/**
 * interface to get config
 *
 * @Author IpMan
 * @Date 2024/5/4 19:11
 */
public interface IMRepository {

    /**
     * 获取默认配置仓库实例。
     * 通过给定的配置元数据初始化配置仓库。
     *
     * @param meta 配置元数据，描述配置源的相关信息。
     * @return 返回默认配置仓库实例。
     */
    static IMRepository getDefault(ConfigMeta meta) {
        return new IMRepositoryImpl(meta);
    }

    /**
     * 获取当前所有配置。
     * 该方法用于一次性获取配置源中的所有配置项。
     *
     * @return 返回包含所有配置项的Map，配置项的键为配置名，值为配置值。
     */
    Map<String, String> getConfig();

    /**
     * 添加配置变更监听器。
     * 通过添加监听器，可以监听配置项的变更事件。
     *
     * @param listener 配置变更监听器实例。
     */
    void addListener(IMRepositoryChangeListener listener);


}
