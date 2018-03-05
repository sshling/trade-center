package cn.shaolingweb.rml.tradecenter;

import cn.shaolingweb.rml.tradecenter.domain.Hq;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class TradeCenterApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradeCenterApplication.class, args);
	}
}
