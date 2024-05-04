package cn.ipman.config.client.repository;

import cn.ipman.config.client.meta.ConfigMeta;

import java.util.Map;

public interface IMRepositoryChangeListener {

    /**
     * 配置发生变化时的回调方法。
     *
     * @param changeEvent 包含配置元数据和新配置信息的事件对象。
     *                    - meta: 配置的元数据，描述了配置的相关信息。
     *                    - config: 新的配置信息，以键值对的形式存储。
     */
    void onChange(ChangeEvent changeEvent);

    /**
     * ChangeEvent 类是一个记录类（JDK 16及以上版本特性），用于封装配置变化事件的信息。
     * 包含配置的元数据和新配置的数据。
     */
    record ChangeEvent(ConfigMeta meta, Map<String, String> config) {};


    //  如果jdk版本低于16, 不兼容record, 以下是Java8的实现
    //    @Data
    //    @AllArgsConstructor
    //    class ChangeEvent {
    //        private ConfigMeta meta;
    //        private Map<String, String> config;
    //    }
}
