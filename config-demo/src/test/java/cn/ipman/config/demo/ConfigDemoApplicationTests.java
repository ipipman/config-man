package cn.ipman.config.demo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import cn.ipman.config.server.ConfigServerApplication;


@SpringBootTest(classes = {ConfigDemoApplication.class})
class ConfigDemoApplicationTests {

    static ApplicationContext context1;

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
    }

    @Test
    void contextLoads() {


        System.out.println("config demo running ... ");
    }


    @AfterAll
    static void destroy() {
        System.out.println(" ===========     close spring context     ======= ");
        SpringApplication.exit(context1, () -> 1);
    }

}
