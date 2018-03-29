package cn.shaolingweb.rml.tradecenter.service;

import cn.shaolingweb.rml.tradecenter.domain.AlarmConf;
import cn.shaolingweb.rml.tradecenter.domain.Hq;
import cn.shaolingweb.rml.tradecenter.domain.QueryType;
import cn.shaolingweb.rml.tradecenter.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通过新浪查询接口查询
 */
@Service
public class HangqingSinaServiceImpl implements HangqingService {
    private static Logger logger = LoggerFactory.getLogger(HangqingSinaServiceImpl.class);
    @Autowired
    private AlarmSender alarmSender;

    @Override
    public boolean query(List<String> codes) {
        String responseStr = HttpUtil.getStr(QueryType.GEGU, codes);
        Map<String, AlarmConf> allConf = AlarmConfService.alarmConfMap;
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
                } else {
                    logger.warn("查询异常:" + item);
                }
            }
        }

        if (queryType.equals(QueryType.GEGU)) {
            //假设当前是请求一条的数据 TODO ,readonly
            Map<String, AlarmConf> map = AlarmConfService.alarmConfMap;
            for (String item : clearData) {
                /*
                var hq_str_sz300525="博思软件,50.880,46.850,51.540,51.540,50.000,51.540,0.000,1459025,74744533.500,740775,51.540,8800,51.530,1100,51.500,6100,51.400,200,51.040,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,2018-03-29,11:05:03,00";

                博思软件,50.880,46.850,51.540,51.540,50.000,51.540,0.000,1459025,74744533.500,740775,51.540,8800,51.530,1100,51.500,6100,51.400,200,51.040,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,2018-03-29,11:05:03,00
                 */
                String[] arr = item.split(",");
                if (arr.length < 20) {
                    logger.error("异常数据:" + item);
                    continue;
                }
                Hq hq = new Hq();
                hq.setName(arr[0]);
                //hq.setCode(arr[1]);
                hq.setPriceCurrent(Double.valueOf(arr[3]));
                hq.setPriceMax(Double.valueOf(arr[4]));
                hq.setPriceMin(Double.valueOf(arr[5]));
                hq.setVolumeTraded(Long.valueOf(arr[8]));//TODO 除100 -->手数
                Long b1 = Long.valueOf(arr[10]);
                b1 = Long.valueOf(Math.round(b1 / 100));//买1,减少到手
                hq.setB1Vol(b1);
                Long s1 = Long.valueOf(arr[20]);
                s1 = Long.valueOf(Math.round(s1 / 100));//卖1,减少到手
                hq.setS1Vol(s1);//卖1
                logger.info("行情-->" + hq.toString());
                AlarmConf conf = map.get(hq.getName());
                if (conf != null) {
                    hq.setCode(conf.getCode());
                    //报警的买1量 > 当前的买1量
                    if (conf.getB1Vol() != 0 && conf.getB1Vol() > hq.getB1Vol()) {
                        String msg = String.format("当前买1量[%s]<阀值[%s],将打开涨停", hq.getB1Vol(), conf.getB1Vol());
                        alarmSender.alerm(hq, msg);
                    } else {
                        logger.info("买1量[{}]在阀值[{}]之上.", hq.getB1Vol(), conf.getB1Vol());
                    }
                    //报警的卖1量 > 当前的卖1量
                    if (conf.getS1Vol() != 0 && conf.getS1Vol() > hq.getS1Vol()) {
                        alarmSender.alerm(hq, "当前卖1量<阀值,将开跌停");
                    }

                    if (conf.getUp() != 0 && conf.getUp() < hq.getPriceCurrent()) {//TODO 维持一定时间
                        alarmSender.alerm(hq, "现价>上界阈值");
                    }
                    if (conf.getDown() != 0 && conf.getDown() > hq.getPriceCurrent()) {
                        alarmSender.alerm(hq, "现价<下界阈值");
                    }
                } else {
                    logger.warn("未配置:" + hq.getCode() + ":" + hq.getName());
                }
                result.add(hq);
            }
        }
        return result;

    }
}
