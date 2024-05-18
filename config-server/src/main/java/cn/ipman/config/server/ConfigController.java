package cn.ipman.config.server;

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
 * Description for this class
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

    Map<String, Long> VERSION = new HashMap<>();

    MultiValueMap<String, DeferredResult<Long>> appKeyDeferredResult = new LinkedMultiValueMap<>();

    static String getAppKey(String app, String env, String ns) {
        return app + "-" + env + "-" + ns;
    }

    @RequestMapping("/list")
    public List<Configs> list(@RequestParam("app") String app,
                              @RequestParam("env") String env,
                              @RequestParam("ns") String ns) {
        return mapper.list(app, env, ns);
    }

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

    private void insertOrUpdate(Configs configs) {
        Configs conf = mapper.select(configs.getApp(), configs.getEnv(), configs.getNs(), configs.getPkey());
        if (conf == null) {
            mapper.insert(configs);
        } else {
            mapper.update(configs);
        }
    }

    @GetMapping("/version")
    public DeferredResult<Long> version(@RequestParam("app") String app,
                                        @RequestParam("env") String env,
                                        @RequestParam("ns") String ns) {
        String appKey = getAppKey(app, env, ns);
        log.info("config version poll {}", appKey);
        log.debug("config version poll in defer debug {}", appKey);

        // 延迟返回
        DeferredResult<Long> deferredResult = new DeferredResult<>(10_000L);
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


    @GetMapping("/status")
    public boolean version() {
        return distributedLocks.getLocked().get();
    }


}
