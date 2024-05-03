package cn.ipman.config.client;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import({IMConfigRegistry.class})
public @interface EnableIpManConfig {


}
