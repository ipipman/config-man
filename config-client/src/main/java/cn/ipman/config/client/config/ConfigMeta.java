package cn.ipman.config.client.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/5/4 19:23
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ConfigMeta {

    String app;
    String env;
    String ns;
    String configServer;


}
