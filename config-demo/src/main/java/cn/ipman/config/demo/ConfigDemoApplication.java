package cn.ipman.config.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties({DemoConfig.class})
public class ConfigDemoApplication {

    @Value("${ipman.a}")
    private String a;

    @Autowired
    private DemoConfig demoConfig;

    public static void main(String[] args) {
        SpringApplication.run(ConfigDemoApplication.class, args);
    }


    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            System.out.println(a);
            System.out.println(demoConfig.getA());
        };
    }

}
