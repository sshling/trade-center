package cn.shaolingweb.rml.tradecenter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication //
@EnableAutoConfiguration//配置
@EnableScheduling//调度
@Slf4j
public class TradeCenterApplication {

    public static void main(String[] args) {
        log.info("启动应用...");
        SpringApplication.run(TradeCenterApplication.class, args);
        log.info("启动应用完成.");
    }
}
