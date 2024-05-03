package cn.ipman.config.client.config;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/5/3 22:42
 */
public interface IMConfigService {

    String[] getPropertyNames();

    String getProperty(String name);

}
