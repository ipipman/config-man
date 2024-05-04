package cn.ipman.config.client.config;

import cn.ipman.config.client.repository.IMRepository;
import cn.ipman.config.client.repository.IMRepositoryChangeListener;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/5/3 22:42
 */
public interface IMConfigService extends IMRepositoryChangeListener {

    static IMConfigService getDefault(ApplicationContext applicationContext, ConfigMeta meta) {
        IMRepository repository = IMRepository.getDefault(meta);
        Map<String, String> config = repository.getConfig();
        IMConfigService configService = new IMConfigServiceImpl(applicationContext, config);
        repository.addListener(configService);
        return configService;
    }

    String[] getPropertyNames();

    String getProperty(String name);



}
