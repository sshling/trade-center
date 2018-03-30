package cn.shaolingweb.rml.tradecenter.util;

import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AppDateUtils {
    //1分钟
    public static final long MIN_1 = TimeUnit.MINUTES.toMinutes(1);
    public static final String FMT = "yyyy-MM-dd HH:mm:ss";
    public static final String FMT_HOUR_MIN = "HH:mm";

    public static String dateToStr(Date date){
        return new SimpleDateFormat(FMT).format(date);
    }
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
    public static boolean tradeTime(){
        if (between("09:20", "11:30", "13:00", "15:00")) {
            return true;
        }
        if (log.isInfoEnabled()) {
            log.info("非交易时间");
        }
        return false;
    }
    //当前是否是在指定的时间内.交易时间:9:24~15:00 ,排除  11:30~13:00
    public static boolean between(String f1, String t1, String f2, String t2) {
        DateFormat df = new SimpleDateFormat(FMT_HOUR_MIN);
        Date now = new Date();
        try {
            now = df.parse(df.format(now));//must
            Date f1D = df.parse(f1);
            Date t1D = df.parse(t1);
            Date f2D = df.parse(f2);
            Date t2D = df.parse(t2);
            boolean ff1 = now.after(f1D);//
            boolean ff2 = now.before(t1D);//
            boolean ff3 = now.after(f2D);//true
            boolean ff4 = now.before(t2D);//
            boolean ok1=now.after(f1D) && now.before(t1D);//上午盘
            boolean ok2=now.after(f2D) && now.before(t2D);//下午盘
            if (ok1|| ok2) {//盘中
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void main(String[] args) throws Exception {
        DateFormat df = new SimpleDateFormat(FMT);
        Date last = df.parse("2018-03-30 11:55:00");
        df = new SimpleDateFormat(FMT_HOUR_MIN);
        String nowStr = df.format(new Date());

        if (tradeTime()) {
            System.out.println(String.format("交易时间内:[%s]", nowStr));
        }else {
            System.err.println(String.format("交易时间外:[%s]", nowStr));
        }


    }
}
