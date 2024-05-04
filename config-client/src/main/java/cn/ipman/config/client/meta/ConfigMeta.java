package cn.ipman.config.client.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ConfigMeta 类用于存储配置服务的元数据信息。
 * 该类包含了应用名称、环境、命名空间和配置服务器地址等信息。
 *
 * @Author IpMan
 * @Date 2024/5/4 19:23
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ConfigMeta {

    String app;      // 应用名称
    String env;      // 环境标识
    String ns;       // 命名空间
    String configServer;   // 配置服务器地址

    /**
     * 生成配置项的键。
     * 将应用名称、环境和命名空间以"_"连接起来形成一个唯一键。
     *
     * @return String 返回配置项的键。
     */
    public String genKey() {
        return this.getApp() + "_" + this.getEnv() + "_" + this.getNs();
    }

    /**
     * 获取配置列表的请求路径。
     *
     * @return String 返回配置列表的HTTP请求路径。
     */
    public String listPath() {
        return this.path("list");
    }

    /**
     * 获取配置版本信息的请求路径。
     *
     * @return String 返回配置版本信息的HTTP请求路径。
     */
    public String versionPath() {
        return this.path("version");
    }

    /**
     * 构建配置服务请求的通用路径。
     *
     * @param context 请求的上下文路径。
     * @return String 返回配置服务请求的完整路径。
     */
    private String path(String context) {
        return this.getConfigServer() + "/" + context + "?app=" + this.getApp()
                + "&env=" + this.getEnv() + "&ns=" + this.getNs();
    }


}
