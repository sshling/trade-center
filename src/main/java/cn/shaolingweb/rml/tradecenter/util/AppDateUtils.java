package cn.shaolingweb.rml.tradecenter.util;

import org.springframework.util.Assert;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AppDateUtils {
    //1分钟
    public static final long MIN_1 = TimeUnit.MINUTES.toMinutes(1);
    public static final String FMT = "yyyy-MM-dd HH:mm:ss";

    //计算之前某个时间到当前的时间,是否有足够的间隔
    //从上次到现在保持在30秒内
    public static boolean timeOutSecond(Date from, int interval) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(from);
        cal.add(Calendar.SECOND, interval);
        return cal.after(Calendar.getInstance());
    }

    public static boolean timeOutSecond(Date from, int interval, Date to) {
        Calendar fromCal = Calendar.getInstance();
        fromCal.setTime(from);
        fromCal.add(Calendar.SECOND, interval);

        Calendar toCal = Calendar.getInstance();
        toCal.setTime(to);
        return fromCal.after(toCal);
    }

    public static void main(String[] args) throws Exception {
        DateFormat df = new SimpleDateFormat(FMT);
        Date last = df.parse("2018-03-30 11:55:00");
        //Assert.isTrue(timeOutSecond(last, 60), "断言失败:间隔超过30s");
    }
}
