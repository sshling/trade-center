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
import java.util.concurrent.ConcurrentHashMap;
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
    private Map<Integer, Date> match = new ConcurrentHashMap<>();

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
        Map<Integer, String> cleanData = new HashMap<>();
        for (String item : allData) {//单条股票数据
            if (!item.startsWith("var hq_str_s_sh000001")) {//不是大盘
                //var hq_str_sh600000="xxx"
                String itemNew = StringUtils.substringBetween(item, "\"", "\"");
                //找code值
                String codeTmp = item.split("=")[0];
                String code = codeTmp.substring(codeTmp.length() - 6);
                if (StringUtils.isNoneBlank(itemNew)) {
                    cleanData.put(Integer.valueOf(code), itemNew);
                } else {
                    logger.warn("查询异常:" + item);
                }
            }
        }

        if (queryType.equals(QueryType.GEGU)) {
            //假设当前是请求一条的数据 TODO ,readonly
            Map<Integer, AlarmConf> map = AlarmConfService.alarmConfMap;
            for (Map.Entry<Integer, String> entry : cleanData.entrySet()) {
                Integer code = entry.getKey();
                String item = entry.getValue();
                String[] arr = item.split(",");
                if (arr.length < 20) {
                    logger.error("异常数据:" + item);
                    continue;
                }
                Hq hq = new Hq();
                hq.setCode(code);
                hq.setName(arr[0]);
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
                AlarmConf conf = map.get(hq.getCode());
                if (conf != null) {
                    hq.setCode(conf.getCode());

                    if (zhangting_dieting(hq, conf)) {//涨跌停的处理
                        continue;
                    }
                    //上突:
                    if (conf.getUp() != 0) {
                        if (conf.getUp() <= hq.getPriceCurrent()) {//大于等于阈值
                            if (conf.getUpKeepTime() == 0) {
                                String msg = String.format("现价>=上界阈值,立即报警:突破");
                                alarmSender.alerm(hq, msg);
                                continue;
                            } else {//需要维持一段时间
                                if (!match.containsKey(hq.getCode())) {//首次
                                    String msg = String.format("现价>=上界阈值,等待超时");
                                    logger.warn(msg);
                                    match.put(hq.getCode(), new Date());
                                } else {//注:若一运行就已经过了阈值,则很快就超时了,因为第一次计算时间较晚
                                    Date first = match.get(hq.getCode());
                                    if (AppDateUtils.timeOutSecond(first, conf.getUpKeepTime())) {
                                        String msg = String.format("现价>=上界阈值,超时[%s from-to:%s - %s]:突破",
                                                conf.getUpKeepTimeStr(), AppDateUtils.dateToStr(first)
                                                , AppDateUtils.dateToStr(new Date()));
                                        alarmSender.alerm(hq, msg);
                                        continue;
                                    }

                                }
                            }
                        } else {
                            match.remove(conf.getCode());//若不符合了,要清理
                        }
                    }
                    //下破:
                    if (conf.getDown() != 0) {
                        if (conf.getDown() >= hq.getPriceCurrent()) {//现价 小于等于 下界阈值
                            if (conf.getDownKeepTime() == 0) {
                                alarmSender.alerm(hq, "现价<=下界阈值:下破");
                                continue;
                            } else {//需要维持一段时间
                                if (!match.containsKey(hq.getCode())) {//首次
                                    match.put(hq.getCode(), new Date());
                                    String msg = String.format("现价<=下界阈值,等待超时");
                                    logger.warn(msg);
                                } else {
                                    Date first = match.get(hq.getCode());
                                    if (AppDateUtils.timeOutSecond(first, conf.getDownKeepTime())) {
                                        String msg = String.format("现价<=下界阈值,超时[%s]:下破", conf.getUpKeepTimeStr());
                                        alarmSender.alerm(hq, msg);
                                        continue;
                                    }
                                }
                            }
                        } else {
                            match.remove(conf.getCode());//若不符合了,要清理
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
            if (logger.isDebugEnabled()) {
                logger.debug("买1量[{}]在阀值[{}]之上.", hq.getB1(), conf.getB1Vol());
            }
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
