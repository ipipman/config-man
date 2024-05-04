package cn.ipman.config.client.repository;

import cn.ipman.config.client.config.ConfigMeta;
import cn.ipman.config.client.utils.HttpUtils;
import com.alibaba.fastjson.TypeReference;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/5/4 19:12
 */
public class IMRepositoryImpl implements IMRepository {

    ConfigMeta meta;
    Map<String, Long> versionMap = new HashMap<>();
    Map<String, Map<String, String>> configMap = new HashMap<>();


    public IMRepositoryImpl(ConfigMeta meta) {
        this.meta = meta;
        executor.scheduleWithFixedDelay(this::heartbeat, 1000, 5000, TimeUnit.MILLISECONDS);

    }

    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @Override
    public Map<String, String> getConfig() {
        String key = meta.genKey();
        if (configMap.containsKey(key)) {
            return configMap.get(key);
        }
        return findAll();
    }

    private @NotNull Map<String, String> findAll() {
        String listPath = meta.listPath();
        System.out.println("[IM_CONFIG] list all configs from ipman config server.");
        List<Configs> configs = HttpUtils.httpGet(listPath, new TypeReference<List<Configs>>() {
        });
        Map<String, String> resultMap = new HashMap<>();
        configs.forEach(c -> resultMap.put(c.getPkey(), c.getPval()));
        return resultMap;
    }

    private void heartbeat() {
        String versionPath = meta.versionPath();
        Long version = HttpUtils.httpGet(versionPath, new TypeReference<Long>() {
        });
        String key = meta.genKey();
        Long oldVersion = versionMap.getOrDefault(key, -1L);
        if (version > oldVersion) {
            System.out.println("[IM_CONFIG] current=" + version + ", old=" + oldVersion);
            System.out.println("[IM_CONFIG] need update new configs.");
            versionMap.put(key, version);
            configMap.put(key, findAll());
        }

    }


}
