package cn.shaolingweb.rml.tradecenter.service;

import cn.shaolingweb.rml.tradecenter.domain.AlarmConf;
import cn.shaolingweb.rml.tradecenter.domain.Hq;
import cn.shaolingweb.rml.tradecenter.domain.QueryType;
import cn.shaolingweb.rml.tradecenter.util.AppDateUtils;
import cn.shaolingweb.rml.tradecenter.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 通过新浪查询接口查询
 */
@Service
public class HangqingSinaServiceImpl implements HangqingService {
    private static Logger logger = LoggerFactory.getLogger(HangqingSinaServiceImpl.class);
    @Autowired
    private AlarmSender alarmSender;
    //key=code ,value=Date 第一次符合条件触发时间
    private Map<Integer, Date> match = new HashMap<>();

    @Override
    public boolean query(List<Integer> codes) {
        String responseStr = HttpUtil.getStr(QueryType.GEGU, codes);
        Map<Integer, AlarmConf> allConf = AlarmConfService.alarmConfMap;
        List<Hq> result = repToObj(QueryType.GEGU, responseStr);
        for (Hq hq : result) {
            logger.info(hq.toString());
        }
        return true;
    }

    @Override
    public boolean queryAll(List<Integer> code) {
        return true;
    }

    public static String getUrl(QueryType queryType, List<Integer> codes) {

        String codesStr = codes.stream().map(item -> {
            if (item >= 600000) {//上证
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
            Map<Integer, AlarmConf> map = AlarmConfService.alarmConfMap;
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
                hq.setB1(b1);
                Long s1 = Long.valueOf(arr[20]);
                s1 = Long.valueOf(Math.round(s1 / 100));//卖1,减少到手
                hq.setS1(s1);//卖1
                logger.info("行情-->" + hq.toString());
                AlarmConf conf = map.get(hq.getName());
                if (conf != null) {
                    hq.setCode(conf.getCode());

                    if (zhangting_dieting(hq, conf)) {//涨跌停的处理
                        continue;
                    }
                    //上突:
                    if (conf.getUp() != 0 && conf.getUp() < hq.getPriceCurrent()) {
                        if (conf.getUpKeepTime() == 0) {
                            alarmSender.alerm(hq, "现价>上界阈值,立即报警:突破");
                            continue;
                        } else {//需要维持一段时间
                            if (!match.containsKey(hq.getCode())) {//首次
                                match.put(hq.getCode(), new Date());
                            } else {
                                Date first = match.get(hq.getCode());
                                if (AppDateUtils.timeOutSecond(first, conf.getUpKeepTime())) {
                                    String msg = String.format("现价>上界阈值,超时[%s]:突破", conf.getUpKeepTimeStr());
                                    alarmSender.alerm(hq, msg);
                                    continue;
                                }

                            }
                        }
                    }
                    //下破:
                    if (conf.getDown() != 0 && conf.getDown() > hq.getPriceCurrent()) {
                        if (conf.getDownKeepTime() == 0) {
                            alarmSender.alerm(hq, "现价<下界阈值:下破");
                            continue;
                        } else {//需要维持一段时间
                            if (!match.containsKey(hq.getCode())) {//首次
                                match.put(hq.getCode(), new Date());
                            } else {
                                Date first = match.get(hq.getCode());
                                if (AppDateUtils.timeOutSecond(first, conf.getDownKeepTime())) {
                                    String msg = String.format("现价<下界阈值,超时[%s]:下破", conf.getUpKeepTimeStr());
                                    alarmSender.alerm(hq, msg);
                                    continue;
                                }
                            }
                        }
                    }
                } else {
                    logger.warn("未配置:" + hq.getCode() + ":" + hq.getName());
                }
                result.add(hq);
            }
        }
        return result;

    }
    /*
    返回true ,表示已报警,无需向下判断
     */
    private boolean zhangting_dieting(Hq hq, AlarmConf conf) {
        //将打开涨停:报警的买1量 > 当前的买1量
        if (conf.getB1Vol() != 0 && conf.getB1Vol() > hq.getB1()
                && hq.getS1() == 0 //卖1量未0,否则已经打开
                ) {
            String msg = String.format("当前买1量[%s]<阀值[%s],将打开涨停", hq.getB1(), conf.getB1Vol());
            alarmSender.alerm(hq, msg);
            return true;
        } else {
            logger.info("买1量[{}]在阀值[{}]之上.", hq.getB1(), conf.getB1Vol());
        }
        //将打开跌停:报警的卖1量 > 当前的卖1量
        if (conf.getS1Vol() != 0 && conf.getS1Vol() > hq.getS1()
                && hq.getB1() == 0 //买
                ) {
            alarmSender.alerm(hq, "当前卖1量<阀值,将开跌停");
            return true;
        }
        return false;
    }
}
