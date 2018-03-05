package cn.shaolingweb.rml.tradecenter.service;

import cn.shaolingweb.rml.tradecenter.domain.Hq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlarmSenderImpl implements AlarmSender {
    private final Logger logger = LoggerFactory.getLogger(getClass());

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
        logger.info("报警:"+msg+", 当前行情:"+hq.toString());
        return false;
    }
}
