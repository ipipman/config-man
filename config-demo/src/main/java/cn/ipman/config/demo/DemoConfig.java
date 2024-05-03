package cn.ipman.config.demo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/5/3 21:24
 */
@Data
@ConfigurationProperties(prefix = "ipman")
public class DemoConfig {

    private String a;

}
