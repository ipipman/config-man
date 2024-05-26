package cn.ipman.config.server.api;

import cn.ipman.config.server.dal.ConfigsMapper;
import cn.ipman.config.server.election.DistributedLocks;
import cn.ipman.config.server.model.Configs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置服务控制器，提供配置的查询、更新和版本查询功能
 *
 * @Author IpMan
 * @Date 2024/4/27 08:18
 */
@RestController
@Slf4j
public class ConfigController {

    @Autowired
    ConfigsMapper mapper;

    @Autowired
    DistributedLocks distributedLocks;

    // 用于存储配置的版本信息
    Map<String, Long> VERSION = new HashMap<>();

    // 用于存储appKey与DeferredResult之间的映射，以支持异步返回配置版本信息
    MultiValueMap<String, DeferredResult<Long>> appKeyDeferredResult = new LinkedMultiValueMap<>();

    // 生成应用键
    static String getAppKey(String app, String env, String ns) {
        return app + "-" + env + "-" + ns;
    }

    /**
     * 查询配置列表。
     *
     * @param app 应用名称
     * @param env 环境标识
     * @param ns 命名空间
     * @return 配置列表
     */
    @RequestMapping("/list")
    public List<Configs> list(@RequestParam("app") String app,
                              @RequestParam("env") String env,
                              @RequestParam("ns") String ns) {
        return mapper.list(app, env, ns);
    }

    /**
     * 更新配置。
     *
     * @param app 应用名称
     * @param env 环境标识
     * @param ns 命名空间
     * @param params 要更新的配置参数映射
     * @return 更新后的配置列表
     */
    @RequestMapping("/update")
    public List<Configs> update(@RequestParam("app") String app,
                                @RequestParam("env") String env,
                                @RequestParam("ns") String ns,
                                @RequestBody Map<String, String> params) {
        String appKey = getAppKey(app, env, ns);
        log.info("config update. push {} {}", app, params);
        log.debug("config update. push in defer debug {} {}", app, params);

        // 查询或更新配置, 并更新版本号
        params.forEach((k, v) -> insertOrUpdate(new Configs(app, env, ns, k, v)));
        VERSION.put(appKey, System.currentTimeMillis());

        // 如果有配置更新, 返回获取版本 /version 的请求
        List<DeferredResult<Long>> deferredResults = appKeyDeferredResult.get(appKey);
        if (deferredResults != null) {
            deferredResults.forEach(deferredResult -> {
                Long version = VERSION.getOrDefault(appKey, -1L);
                deferredResult.setResult(version);
                log.debug("config version poll set defer for {} {}", ns, version);
            });
        }
        return mapper.list(app, env, ns);
    }

    /**
     * 插入或更新配置项
     * @param configs 查询或更新配置
     */
    private void insertOrUpdate(Configs configs) {
        Configs conf = mapper.select(configs.getApp(), configs.getEnv(), configs.getNs(), configs.getPkey());
        if (conf == null) {
            mapper.insert(configs);
        } else {
            mapper.update(configs);
        }
    }

    /**
     * 异步查询配置版本。
     *
     * @param app 应用名称
     * @param env 环境标识
     * @param ns 命名空间
     * @return DeferredResult，异步返回配置的版本号
     */
    @GetMapping("/version")
    public DeferredResult<Long> version(@RequestParam("app") String app,
                                        @RequestParam("env") String env,
                                        @RequestParam("ns") String ns) {
        String appKey = getAppKey(app, env, ns);
        log.info("config version poll {}", appKey);
        log.debug("config version poll in defer debug {}", appKey);

        // 创建并返回一个异步结果对象，用于后续通知
        DeferredResult<Long> deferredResult = new DeferredResult<>();
        deferredResult.onCompletion(() -> {
            System.out.println("onCompletion");
            appKeyDeferredResult.remove(appKey);
        });
        deferredResult.onTimeout(() -> {
            System.out.println("onTimeout");
            appKeyDeferredResult.remove(appKey);
        });
        deferredResult.onError((Throwable t) -> {
            System.out.println("onError");
            appKeyDeferredResult.remove(appKey);
        });
        appKeyDeferredResult.add(appKey, deferredResult);
        log.debug("return defer for {}", ns);
        return deferredResult;
        //return VERSION.getOrDefault(appKey, -1L);
    }

    /**
     * 查询分布式锁的状态。
     *
     * @return 分布式锁是否锁定的状态（true表示锁定，false表示未锁定）
     */
    @GetMapping("/status")
    public boolean version() {
        return distributedLocks.getLocked().get();
    }


}
