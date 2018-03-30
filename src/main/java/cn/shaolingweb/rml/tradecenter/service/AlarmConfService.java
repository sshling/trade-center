package cn.shaolingweb.rml.tradecenter.service;

import cn.shaolingweb.rml.tradecenter.domain.AlarmConf;
import cn.shaolingweb.rml.tradecenter.util.JsonUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
任务的配置管理
 */
@Service
public class AlarmConfService {
    public static Logger logger = LoggerFactory.getLogger(AlarmConfService.class);

    private static List<AlarmConf> alarmConfs;
    //key==股票代码,
    public static Map<Integer, AlarmConf> alarmConfMap = new ConcurrentHashMap<>();

    static {
        reload();
    }

    public static void reload() {
        Resource resource = new ClassPathResource("alarm/alarm-conf.json");
        String confStr = null;
        try {//读取配置文件的内容
            confStr = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
            logger.info("conf:" + confStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        alarmConfs = JsonUtil.toList(confStr);
        for (AlarmConf conf : alarmConfs) {
            String codeStr = conf.getCode() + "";
            if (6 != codeStr.length()) {
                String msg="配置出错,code:"+codeStr;
                logger.error(msg);
                throw new IllegalArgumentException(msg);
            }
            String upStr = conf.getUpKeepTimeStr();
            if (upStr.endsWith("m")) {
                String time = upStr.substring(0, upStr.length() - 2);
                conf.setUpKeepTime(Integer.valueOf(time)*60);//转为秒
            }
            String downStr = conf.getDownKeepTimeStr();
            if (downStr.endsWith("m")){
                String time = downStr.substring(0, downStr.length() - 2);
                conf.setDownKeepTime(Integer.valueOf(time)*60);
            }
            alarmConfMap.put(conf.getCode(), conf);
        }
    }
}
