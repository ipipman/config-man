package cn.ipman.config.client.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/4/27 08:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Configs {

    private String app;     // 配置中心server返回的, 应用名
    private String env;     // 配置中心server返回的, 环境
    private String ns;      // 配置中心server返回的, 命名空间
    private String pkey;    // 配置中心server返回的, 配置的key
    private String pval;    // 配置中心server返回的, 配置的val
}
