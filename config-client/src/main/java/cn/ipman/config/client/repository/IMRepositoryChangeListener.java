package cn.ipman.config.client.repository;

import cn.ipman.config.client.config.ConfigMeta;

import java.util.Map;

public interface IMRepositoryChangeListener {

    void onChange(ChangeEvent changeEvent);

    record ChangeEvent(ConfigMeta meta, Map<String, String> config) {};


//    @Data
//    @AllArgsConstructor
//    class ChangeEvent {
//        private ConfigMeta meta;
//        private Map<String, String> config;
//    }
}
