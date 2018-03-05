package cn.shaolingweb.rml.tradecenter.service;

import cn.shaolingweb.rml.tradecenter.domain.AlarmConf;
import cn.shaolingweb.rml.tradecenter.util.HttpUtil;
import cn.shaolingweb.rml.tradecenter.util.JsonUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AlarmConfService {
    public static Logger logger = LoggerFactory.getLogger(AlarmConfService.class);

    private static List<AlarmConf> alarmConfs;
    public static Map<String, AlarmConf> alarmConfMap = new ConcurrentHashMap<>();

    static {
        Resource resource=new ClassPathResource("alarm/alarm-conf.json");
        String confStr=null;
        try {
            confStr = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
            logger.info("conf:"+confStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        alarmConfs=JsonUtil.toList(confStr);
        for (AlarmConf alarmConf : alarmConfs) {
            alarmConfMap.put(alarmConf.getCode()+"", alarmConf);
        }
    }


}
