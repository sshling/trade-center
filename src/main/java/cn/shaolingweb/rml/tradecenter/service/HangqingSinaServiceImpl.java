package cn.shaolingweb.rml.tradecenter.service;

import cn.shaolingweb.rml.tradecenter.domain.AlarmConf;
import cn.shaolingweb.rml.tradecenter.domain.Hq;
import cn.shaolingweb.rml.tradecenter.domain.QueryType;
import cn.shaolingweb.rml.tradecenter.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 通过新浪查询接口查询
 */
@Service
public class HangqingSinaServiceImpl implements HangqingService {
    private static Logger logger = LoggerFactory.getLogger(HangqingSinaServiceImpl.class);

    private AlarmSender alarmSender;

    @Override
    public boolean query(List<String> codes) {
        String responseStr = HttpUtil.getStr(QueryType.GEGU, codes);

        List<Hq> result = repToObj(QueryType.GEGU, responseStr);
        for (Hq hq : result) {
            logger.info(hq.toString());
        }
        return true;
    }

    @Override
    public boolean queryAll(List<String> code) {
        return true;
    }

    public static String getUrl(QueryType queryType, List<String> codes) {

        String codesStr = codes.stream().map(item -> {
            if (item.startsWith("6")) {//上证
                return "sh" + item;
            }
            return "sz" + item;
        }).collect(Collectors.joining(","));
        StringBuilder sb = new StringBuilder("http://hq.sinajs.cn/list=");
        if (queryType.equals(QueryType.GEGU)) {
            sb.append(codesStr);
        } else {
            sb.append("s_sh000001");
        }
        return sb.toString();
    }

    /**
     * 将响应数据转换为对象
     *
     * @param data
     */
    public List<Hq> repToObj(QueryType queryType, String data) {
        List<Hq> result = new ArrayList<>();
        data = StringUtils.removeAll(data, "\n");
        String[] allData = data.split(";");
        List<String> clearData = new ArrayList<>();

        for (String item : allData) {//单条股票数据
            if (!item.startsWith("var hq_str_s_sh000001")) {//不是大盘
                //var hq_str_sh600000="xxx"
                String itemNew = StringUtils.substringBetween(item, "\"", "\"");
                if (StringUtils.isNoneBlank(itemNew)) {
                    clearData.add(itemNew);
                }else {
                    logger.warn("查询异常:"+item);
                }
            }
        }

        if (queryType.equals(QueryType.GEGU)) {
            //假设当前是请求一条的数据 TODO ,readonly
            Map<String, AlarmConf> map = AlarmConfService.alarmConfMap;
            for (String item : clearData) {
                String[] arr = item.split(",");
                if (arr.length<20) {
                    logger.error("异常数据:"+item);
                    continue;
                }
                Hq hq = new Hq();
                hq.setName(arr[0]);
                hq.setCode(arr[1]);
                hq.setPriceCurrent(Double.valueOf(arr[3]));
                hq.setPriceMax(Double.valueOf(arr[4]));
                hq.setPriceMin(Double.valueOf(arr[5]));
                hq.setVolumeTraded(Long.valueOf(arr[8]));//TODO 除100 -->手数
                hq.setB1Vol(Long.valueOf(arr[10]));
                hq.setS1Vol(Long.valueOf(arr[20]));
                logger.info("行情-->" + hq.toString());
                AlarmConf alarmConf = map.get(hq.getCode());
                if (alarmConf != null) {
                    //报警的买1量 > 当前的买1量
                    if (alarmConf.getB1Vol() > hq.getB1Vol()) {
                        alarmSender.alerm(hq, "当前买1量<阀值,将开跌停");
                    }
                    //报警的卖1量 > 当前的卖1量
                    if (alarmConf.getS1Vol() > hq.getS1Vol()) {
                        alarmSender.alerm(hq, "当前卖1量<阀值,将开涨停");
                    }

                    if (alarmConf.getUp() < hq.getPriceCurrent()) {//TODO 维持一定时间
                        alarmSender.alerm(hq, "现价>上界阈值");
                    }
                    if (alarmConf.getDown() > hq.getPriceCurrent()) {
                        alarmSender.alerm(hq, "现价<下界阈值");
                    }
                }
                result.add(hq);
            }
        }
        return result;

    }
}
