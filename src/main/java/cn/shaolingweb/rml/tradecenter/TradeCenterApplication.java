package cn.shaolingweb.rml.tradecenter;

import cn.shaolingweb.rml.tradecenter.domain.Hq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
@Slf4j
public class TradeCenterApplication {

    public static void main(String[] args) {
        log.info("启动应用...");
        SpringApplication.run(TradeCenterApplication.class, args);
        log.info("启动应用完成.");
    }
}
