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

    public String genKey() {
        return this.getApp() + "_" + this.getEnv() + "_" + this.getNs();
    }

    public String listPath() {
        return this.path("list");
    }

    public String versionPath() {
        return this.path("version");
    }

    private String path(String context) {
        return this.getConfigServer() + "/" + context + "?app=" + this.getApp()
                + "&env=" + this.getEnv() + "&ns=" + this.getNs();
    }


}
