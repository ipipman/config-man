package cn.ipman.config.client.repository;

import cn.ipman.config.client.meta.ConfigMeta;
import cn.ipman.config.client.meta.Configs;
import cn.ipman.config.client.utils.HttpUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 实现了IMRepository接口的配置仓库类，用于管理和更新配置数据。
 *
 * @Author IpMan
 * @Date 2024/5/4 19:12
 */
public class IMRepositoryImpl implements IMRepository {
    // 当前配置实例的元数据信息, 列: 应用,环境,命名空间,配置服务信息
    ConfigMeta meta;
    // 存储配置的版本信息
    Map<String, Long> versionMap = new HashMap<>();
    // 存储配置数据
    Map<String, Map<String, String>> configMap = new HashMap<>();
    // 定时任务执行器
    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    // 配置变更监听器列表
    List<IMRepositoryChangeListener> listeners = new ArrayList<>();

    /**
     * 构造函数，初始化配置仓库
     *
     * @param meta 配置元数据，用于指定配置服务的地址和密钥等信息。
     */
    public IMRepositoryImpl(ConfigMeta meta) {
        this.meta = meta;
        // 每隔5秒执行一次心跳检测任务
        // executor.scheduleWithFixedDelay(this::heartbeat, 1000, 5000, TimeUnit.MILLISECONDS);
        new Thread(this::heartbeat).start();
    }

    /**
     * 添加配置变更监听器。
     *
     * @param listener 配置变更监听器实例。
     */
    public void addListener(IMRepositoryChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * 获取所有配置, 第一次初始化时, 通过Config-Server获取
     *
     * @return 返回当前配置的数据映射表。
     */
    @Override
    public Map<String, String> getConfig() {
        String key = meta.genKey();
        if (configMap.containsKey(key)) {
            return configMap.get(key);
        }
        return findAll();
    }

    /**
     * 获取所有配置, 通过Config-Server获取
     *
     * @return 返回从配置服务器获取到的配置数据映射表。
     */
    private @NotNull Map<String, String> findAll() {
        String listPath = meta.listPath();
        System.out.println("[IM_CONFIG] list all configs from ipman config server.");
        List<Configs> configs = HttpUtils.httpGet(listPath, new TypeReference<List<Configs>>() {
        });
        Map<String, String> resultMap = new HashMap<>();
        configs.forEach(c -> resultMap.put(c.getPkey(), c.getPval()));
        return resultMap;
    }

    /**
     * 心跳检测任务, 通过Config-Server获取配置的版本号，用于检测配置版本是否有更新。
     */
    private void heartbeat() {
        while (true) {
            try {
                // 通过请求Config-Server获取配置版本号
                String versionPath = meta.versionPath();
                HttpUtils.OkHttpInvoker okHttpInvoker = new HttpUtils.OkHttpInvoker();
                okHttpInvoker.init(20_000, 128, 300);
                Long version = JSON.parseObject(okHttpInvoker.get(versionPath), new TypeReference<Long>() {
                });

                // 检查是否有配置更新
                String key = meta.genKey();
                Long oldVersion = versionMap.getOrDefault(key, -1L);
                if (version > oldVersion) {
                    System.out.println("[IM_CONFIG] current=" + version + ", old=" + oldVersion);
                    System.out.println("[IM_CONFIG] need update new configs.");
                    versionMap.put(key, version);

                    Map<String, String> newConfigs = findAll();
                    configMap.put(key, newConfigs);
                    // 通知所有监听器配置发生了变更
                    System.out.println("[IM_CONFIG] fire an EnvironmentChangeEvent with keys:" + newConfigs.keySet());
                    listeners.forEach(listener ->
                            listener.onChange(new IMRepositoryChangeListener.ChangeEvent(meta, newConfigs)));
                }
            } catch (Exception e) {
                System.out.println("[IM_CONFIG] loop request new configs.");
            }
        }
    }


}
