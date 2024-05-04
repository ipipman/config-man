package cn.ipman.config.client.repository;

import cn.ipman.config.client.config.ConfigMeta;
import cn.ipman.config.client.utils.HttpUtils;
import com.alibaba.fastjson.TypeReference;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/5/4 19:12
 */
@AllArgsConstructor
public class IMRepositoryImpl implements IMRepository {

    ConfigMeta meta;

    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @Override
    public Map<String, String> getConfig() {
        String listPath = meta.getConfigServer() + "/list?app=" + meta.getApp()
                + "&env=" + meta.getEnv() + "&ns=" + meta.getNs();

        List<Configs> configs = HttpUtils.httpGet(listPath, new TypeReference<List<Configs>>() {
        });
        Map<String, String> resultMap = new HashMap<>();
        configs.forEach(c -> resultMap.put(c.getPkey(), c.getPval()));
        return resultMap;
    }


}
