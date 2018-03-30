package cn.shaolingweb.rml.tradecenter.service.job;

import cn.shaolingweb.rml.tradecenter.domain.QueryType;
import cn.shaolingweb.rml.tradecenter.service.AlarmConfService;
import cn.shaolingweb.rml.tradecenter.service.AlarmSenderImpl;
import cn.shaolingweb.rml.tradecenter.service.HangqingService;
import cn.shaolingweb.rml.tradecenter.util.AppDateUtils;
import cn.shaolingweb.rml.tradecenter.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
        if (!AppDateUtils.tradeTime()) {
            return;
        }
        AlarmConfService.reload();
    }

    @Scheduled(fixedRate = 2000*15)//毫秒: 30秒执行执行一次
    public void hi() {
        if (!AppDateUtils.tradeTime()) {
            return;
        }

        if (!AlarmSenderImpl.SLEEP){
            List<Integer> codes = AlarmConfService.alarmConfMap.entrySet().stream()
                    .map(entry->entry.getValue().getCode())
                    .collect(Collectors.toList());
            hangqingService.query(codes);
        }
    }
}
