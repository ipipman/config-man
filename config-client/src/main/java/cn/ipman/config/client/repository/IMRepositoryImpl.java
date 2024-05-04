package cn.ipman.config.client.repository;

import cn.ipman.config.client.config.ConfigMeta;
import cn.ipman.config.client.utils.HttpUtils;
import com.alibaba.fastjson.TypeReference;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
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
    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    List<IMRepositoryChangeListener> listeners = new ArrayList<>();

    public IMRepositoryImpl(ConfigMeta meta) {
        this.meta = meta;
        executor.scheduleWithFixedDelay(this::heartbeat, 1000, 5000, TimeUnit.MILLISECONDS);
    }

    public void addListener(IMRepositoryChangeListener listener){
        listeners.add(listener);
    }

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
        if (version > oldVersion) { // 如果有更新
            System.out.println("[IM_CONFIG] current=" + version + ", old=" + oldVersion);
            System.out.println("[IM_CONFIG] need update new configs.");
            versionMap.put(key, version);

            Map<String, String> newConfigs = findAll();
            configMap.put(key, newConfigs);
            System.out.println("[IM_CONFIG] fire an EnvironmentChangeEvent with keys:" + newConfigs.keySet());
            listeners.forEach(listener ->
                    listener.onChange(new IMRepositoryChangeListener.ChangeEvent(meta, newConfigs)));
        }


    }


}
