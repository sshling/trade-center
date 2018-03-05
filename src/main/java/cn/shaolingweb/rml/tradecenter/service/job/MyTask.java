package cn.shaolingweb.rml.tradecenter.service.job;

import cn.shaolingweb.rml.tradecenter.service.HangqingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MyTask {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private HangqingService hangqingService;

    @Scheduled(fixedRate = 2000)
    public void hi(){
        logger.info("hi ,jus a test");
        hangqingService.hi();
    }

}
