package cn.shaolingweb.rml.tradecenter.service.job;

import cn.shaolingweb.rml.tradecenter.domain.QueryType;
import cn.shaolingweb.rml.tradecenter.service.HangqingService;
import cn.shaolingweb.rml.tradecenter.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class MyTask {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private HangqingService hangqingService;

    @Scheduled(fixedRate = 5000)
    public void hi(){
        logger.info("hi ,jus a test");
       // hangqingService.query("600000");
        hangqingService.query("603901,300534");


    }

}
