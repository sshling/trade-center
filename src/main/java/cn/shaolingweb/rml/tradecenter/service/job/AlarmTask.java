package cn.shaolingweb.rml.tradecenter.service.job;

import cn.shaolingweb.rml.tradecenter.domain.QueryType;
import cn.shaolingweb.rml.tradecenter.service.AlarmConfService;
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
public class AlarmTask {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private HangqingService hangqingService;

    @Scheduled(fixedRate = 60_000 * 3)//ms,3分钟重新加载一次
    public void reloadConf() {
        AlarmConfService.reload();
    }

    @Scheduled(fixedRate = 2000)
    public void hi() {
        List<String> codes = AlarmConfService.alarmConfMap.keySet().stream()
                .collect(Collectors.toList());
        hangqingService.query(codes);


    }

}
