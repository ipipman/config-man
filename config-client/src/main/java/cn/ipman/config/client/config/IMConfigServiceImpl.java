package cn.ipman.config.client.config;

import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * ipman config service impl
 *
 * @Author IpMan
 * @Date 2024/5/3 22:47
 */
public class IMConfigServiceImpl implements IMConfigService {

    Map<String, String> config;
    ApplicationContext applicationContext;


    public IMConfigServiceImpl(ApplicationContext applicationContext, Map<String, String> config) {
        this.applicationContext = applicationContext;
        this.config = config;
    }

    @Override
    public String[] getPropertyNames() {
        if (this.config == null) {
            return new String[]{};
        }
        return this.config.keySet().toArray(new String[0]);
    }

    @Override
    public String getProperty(String name) {
        return this.config.getOrDefault(name, null);
    }

    @Override
    public void onChange(ChangeEvent changeEvent) {
        this.config = changeEvent.config();
        if (!config.isEmpty()) {
            System.out.println("[IM_CONFIG] fire an EnvironmentChangeEvent with keys:" + config.keySet());
            applicationContext.publishEvent(new EnvironmentChangeEvent(config.keySet()));
        }
    }
}

