package cn.shaolingweb.rml.tradecenter.util;

import cn.shaolingweb.rml.tradecenter.domain.Hq;
import cn.shaolingweb.rml.tradecenter.domain.QueryType;
import cn.shaolingweb.rml.tradecenter.service.HangqingSinaServiceImpl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * http://square.github.io/okhttp/
 * <p>
 * <pre>
 *    http://hq.sinajs.cn/list=sz000001
 *
 * 返回结果:
 * var hq_str_sz000001="平安银行,9.170,9.190,9.060,9.180,9.050,9.060,9.070,42148125,384081266.460,624253,9.060,638540,9.050,210600,9.040,341700,9.030,2298300,9.020,227184,9.070,178200,9.080,188240,9.090,293536,9.100,295300,9.110,2016-09-14,15:11:03,00";
 *
 * 逗号分割:
 *
 * 不同含义的数据用逗号隔开了，按照程序员的思路，顺序号从0开始。
 * 0：“平安银行”，股票名字；
 * 1：“9.170”，今日开盘价；
 * 2：“9.190”，昨日收盘价；
 * 3：“9.060”，当前价格；
 * 4：“9.180”，今日最高价；
 * 5：“9.050”，今日最低价；
 * 6：“9.060”，竞买价，即“买一“报价；
 * 7：“9.070”，竞卖价，即“卖一“报价；
 * 8：“42148125”，成交的股票数，由于股票交易以一百股为基本单位，所以在使用时，通常把该值除以一百；
 * 9：“384081266.460”，成交金额，单位为“元“，为了一目了然，通常以“万元“为成交金额的单位，所以通常把该值除以一万；
 * 10：“624253”，“买一”申请624253股，即6243手；
 * 11：“9.060”，“买一”报价；
 * 12：“638540”，“买二”申报股数；
 * 13：“9.050”，“买二”报价；
 * 14：“210600”，“买三”申报股数；
 * 15：“9.040”，“买三”报价；
 * 16：“341700”，“买四”申报股数；
 * 17：“9.030”，“买四”报价；
 * 18：“2298300”，“买五”申报股数；
 * 19：“9.020”，“买五”报价；
 * 20：“227184”，“卖一”申报227184股，即2272手；
 * 21：“9.070”，“卖一”报价；
 * (22, 23), (24, 25), (26,27), (28, 29)分别为“卖二”至“卖五”的申报股数及其价格；
 * 30：“2016-09-14”，日期；
 * 31：“15:11:03”，时间；
 *
 *
 * 查询多个股票，在URL后加上一个逗号，再加上股票代码
 * http://hq.sinajs.cn/list=sz000001,sh600000
 *
 * 查询大盘指数，比如查询上证综合指数（000001）：
 * http://hq.sinajs.cn/list=s_sh000001
 *
 * 返回的数据为：
 * var hq_str_s_sh000001="上证指数,3002.8486,-20.6609,-0.68,1334134,14814897";
 * 数据含义分别为：
 * 指数名称，当前点数，当前价格，涨跌率，成交量（手），成交额（万元）；
 *
 * 深圳成指： http://hq.sinajs.cn/list=s_sz399001
 *
 *
 *
 *
 * </pre>
 */
public class HttpUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    private static final OkHttpClient client = new OkHttpClient();

    public static String get(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        String str = null;
        try {
            response = client.newCall(request).execute();
            if (response != null) {
                str = response.body().string();
            }
        } catch (IOException e) {
            logger.error(url, e);
        }
        return str;
    }

    public static String getStr(QueryType queryType, List<String> code) {
        String queryUrl = HangqingSinaServiceImpl.getUrl(queryType, code);
        String rep = get(queryUrl);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("请求URL[%s] 响应:%s", queryUrl, rep));
        }
        return rep;
    }
}
