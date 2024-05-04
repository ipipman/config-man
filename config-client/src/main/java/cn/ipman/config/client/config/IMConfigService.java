package cn.ipman.config.client.config;

import cn.ipman.config.client.repository.IMRepository;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/5/3 22:42
 */
public interface IMConfigService {

    static IMConfigService getDefault(ConfigMeta meta) {
        IMRepository repository = IMRepository.getDefault(meta);
        return new IMConfigServiceImpl(repository.getConfig());
    }

    String[] getPropertyNames();

    String getProperty(String name);

}
