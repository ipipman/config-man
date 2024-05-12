package cn.ipman.config.client.config;

import cn.ipman.config.client.meta.ConfigMeta;
import cn.ipman.config.client.repository.IMRepository;
import cn.ipman.config.client.repository.IMRepositoryChangeListener;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * 配置服务接口，用于管理和提供配置信息。
 * 实现了IMRepositoryChangeListener接口，用于监听配置的变更。
 *
 * @Author IpMan
 * @Date 2024/5/3 22:42
 */
public interface IMConfigService extends IMRepositoryChangeListener {


    /**
     * 获取默认配置服务实例。
     *
     * @param applicationContext 应用上下文，用于获取应用相关资源。
     * @param meta 配置元数据，描述配置的来源和其它必要信息。
     * @return 返回配置服务实例。
     */
    static IMConfigService getDefault(ApplicationContext applicationContext, ConfigMeta meta) {
        // 获取默认配置仓库实例, 从仓库中(远程server服务)上加载配置
        IMRepository repository = IMRepository.getDefault(meta);
        // 从配置中心server,获取配置
        Map<String, String> config = repository.getConfig();

        // 创建配置服务实例, 注册配置变更监听器
        IMConfigService configService = new IMConfigServiceImpl(applicationContext, config);
        repository.addListener(configService);
        return configService;
    }

    /**
     * 获取所有配置属性的名称。
     *
     * @return 返回配置属性名称数组。
     */
    String[] getPropertyNames();

    /**
     * 根据属性名称获取属性值。
     *
     * @param name 属性名称。
     * @return 返回属性值，如果不存在，则返回null。
     */
    String getProperty(String name);

}
