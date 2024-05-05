package cn.ipman.config.demo;

import cn.ipman.config.client.annotation.EnableIpManConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@SpringBootApplication
@EnableConfigurationProperties({DemoConfig.class})
@EnableIpManConfig
@RestController
public class ConfigDemoApplication {

    @Value("${ipman.a}")
    private String a;

    @Value("${ipman.b}")
    private String b;

    @Value("${ipman.c}")
    private String c;

    @Autowired
    private DemoConfig demoConfig;

    public static void main(String[] args) {
        SpringApplication.run(ConfigDemoApplication.class, args);
    }

    @Autowired
    Environment environment;

    @GetMapping("/")
    public String demo() {
        return "ipman.a = " + a + ", \n" +
                "ipman.b = " + b + ", \n" +
                "ipman.c = " + c + ", \n" +
                "ipman.demo.a = " + demoConfig.getA() + ", \n" +
                "ipman.demo.b = " + demoConfig.getB() + ", \n" +
                "ipman.demo.c = " + demoConfig.getC() + ", \n";
    }

    @Bean
    ApplicationRunner applicationRunner() {
        System.out.println(Arrays.toString(environment.getActiveProfiles()));
        return args -> {
            System.out.println(a);
            System.out.println(demoConfig.getA());
        };
    }

}
