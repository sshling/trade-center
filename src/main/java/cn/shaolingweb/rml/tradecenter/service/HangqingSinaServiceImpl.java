package cn.shaolingweb.rml.tradecenter.service;

import cn.shaolingweb.rml.tradecenter.domain.Hq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通过新浪查询接口查询
 */
@Service
public class HangqingSinaServiceImpl  implements HangqingService{
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public Hq query(String code) {
        return null;
    }

    @Override
    public List<Hq> queryAll(List<String> code) {
        return null;
    }

    @Override
    public void hi() {
        logger.info("查询sina行情...");
    }
}
