package cn.shaolingweb.rml.tradecenter.service;

import ch.qos.logback.core.util.TimeUtil;
import cn.shaolingweb.rml.tradecenter.domain.Hq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Time;
import java.util.concurrent.TimeUnit;

@Service
public class AlarmSenderImpl implements AlarmSender {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    public static boolean SLEEP=false;

    @Override
    public boolean weixin() {
        return false;
    }

    @Override
    public boolean openFile() {
        return false;
    }

    @Override
    public boolean callPhone() {
        return false;
    }

    @Override
    public boolean sendMsg() {
        return false;
    }

    @Override
    public boolean sendEmail() {
        return false;
    }

    @Override
    public boolean alerm(Hq hq, String msg) {
        logger.warn("报警:"+msg+", 当前行情:"+hq.toString());
        try {
            Runtime.getRuntime().exec("wps");
            SLEEP=true;
            TimeUnit.SECONDS.sleep(30);
            SLEEP=false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
