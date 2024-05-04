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

    private String app;
    private String env;
    private String ns;
    private String pkey;
    private String pval;
}
