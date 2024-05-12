package cn.ipman.config.demo;

import cn.ipman.config.server.model.Configs;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import cn.ipman.config.server.ConfigServerApplication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SpringBootTest(classes = {ConfigDemoApplication.class})
@Slf4j
class ConfigDemoApplicationTests {

    static ApplicationContext context1;

    @Autowired
    private DemoConfig demoConfig;

    static MockMvc mockMvc;

    @BeforeAll
    static void init() {

        System.out.println(" ================================ ");
        System.out.println(" ============  9129 ============= ");
        System.out.println(" ================================ ");
        System.out.println(" ================================ ");
        context1 = SpringApplication.run(ConfigServerApplication.class,
                "--logging.level.root=info",
                "--logging.level.org.springframework.jdbc=debug",
                "--logging.level.cn.ipman.config=debug",
                "--mybatis.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl",
                "--server.port=9129",
                "--spring.application.name=config-server",
                "--spring.datasource.driver-class-name=org.h2.Driver",
                "--spring.datasource.url=jdbc:h2:mem:h2db",
                "--spring.datasource.username=root",
                "--spring.datasource.password=123456",
                "--spring.sql.init.schema-locations=classpath:db.sql",
                "--spring.sql.init.mode=always",
                "--spring.h2.console.enabled=true",
                "--spring.h2.console.path=/h2",
                "--spring.h2.console.settings.web-allow-others=true"
        );

        mockMvc = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context1).build();
    }

    @Test
    void contextLoads() throws Exception {
        System.out.println("config demo running ... ");

        Map<String, String> configs = new HashMap<>();
        configs.put("ipman.a", "demo1");
        configs.put("ipman.b", "demo2");
        configs.put("ipman.c", "demo3");

        // 模拟调用 config-server 修改配置
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/update?app=app1&env=dev&ns=public")
                                .content(JSON.toJSONString(configs))
                                .contentType("application/json")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        List<Configs> newConfigs = JSON.parseObject(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<Configs>>() {
                }
        );
        System.out.println("config update to " + newConfigs);


        // 验证 config-client 是否将配置也成功更新
        Thread.sleep(5_000 * 2);
        Assertions.assertEquals(configs.get("ipman.a"), demoConfig.getA());
        Assertions.assertEquals(configs.get("ipman.b"), demoConfig.getB());
        Assertions.assertEquals(configs.get("ipman.c"), demoConfig.getC());

    }

    @AfterAll
    static void destroy() {
        System.out.println(" ===========     close spring context     ======= ");
        SpringApplication.exit(context1, () -> 1);
    }

}
