package cn.ipman.config.server;

import cn.ipman.config.server.dal.ConfigsMapper;
import cn.ipman.config.server.model.Configs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
public class ConfigController {

    @Autowired
    ConfigsMapper mapper;

    @Autowired
    DistributedLocks distributedLocks;

    Map<String, Long> VERSION = new HashMap<>();

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

        params.forEach((k, v) -> insertOrUpdate(new Configs(app, env, ns, k, v)));
        VERSION.put(app + "-" + env + "-" + ns, System.currentTimeMillis());
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
    public long version(@RequestParam("app") String app,
                        @RequestParam("env") String env,
                        @RequestParam("ns") String ns) {
        return VERSION.getOrDefault(app + "-" + env + "-" + ns, -1L);
    }


    @GetMapping("/status")
    public boolean version() {
        return distributedLocks.getLocked().get();
    }

}
