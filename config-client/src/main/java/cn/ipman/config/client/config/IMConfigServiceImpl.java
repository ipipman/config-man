package cn.ipman.config.client.config;

import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * IpMan配置服务实现类，用于管理和提供配置信息
 *
 * @Author IpMan
 * @Date 2024/5/3 22:47
 */
public class IMConfigServiceImpl implements IMConfigService {

    // 配置信息
    Map<String, String> config;
    // 应用上下文
    ApplicationContext applicationContext;

    /**
     * 构造函数，初始化配置服务。
     *
     * @param applicationContext 应用上下文，用于发布事件。
     * @param config             初始配置信息。
     */
    public IMConfigServiceImpl(ApplicationContext applicationContext, Map<String, String> config) {
        this.applicationContext = applicationContext;
        this.config = config;
    }

    /**
     * 获取所有配置属性的名称。
     *
     * @return 配置属性名称数组。
     */
    @Override
    public String[] getPropertyNames() {
        if (this.config == null) {
            return new String[]{};
        }
        return this.config.keySet().toArray(new String[0]);
    }

    /**
     * 根据属性名称获取对应的配置值。
     *
     * @param name 属性名称。
     * @return 对应的配置值，如果不存在则返回null。
     */
    @Override
    public String getProperty(String name) {
        return this.config.getOrDefault(name, null);
    }

    /**
     * 配置发生变化时的处理逻辑。
     * 更新配置信息，并发布环境变更事件。
     *
     * @param changeEvent 配置变更事件，包含新的配置信息。
     */
    @Override
    public void onChange(ChangeEvent changeEvent) {
        this.config = changeEvent.config();
        if (!config.isEmpty()) {
            /// 通过 spring-cloud-context 刷新配置
            System.out.println("[IM_CONFIG] fire an EnvironmentChangeEvent with keys:" + config.keySet());
            applicationContext.publishEvent(new EnvironmentChangeEvent(config.keySet()));
        }
    }
}

