package com.aichebaba.rooftop.utils;

import com.jfinal.kit.StrKit;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;
import org.joda.time.DateTime;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 获取日期信息(从数据库获取)
 *
 * @author zhangy
 * @version 1.0
 * SS
 */
public class DateUtil {
    //大写的月份数组
    private static String[] DAXIE_MONTH = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};
    private static Logger logger = Logger.getLogger(DateUtil.class);
    private static String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static String pattern = DEFAULT_PATTERN;

    /**
     * 获取当前日期信息
     *
     * @return 日期类型
     */
    public static Date getDate() {
        Date date = null;
        try {
            date = parse(getDateStr());
        } catch (Exception e) {
            logger.error("获取最新的日期出错：", e);
        }
        return date;
    }

    public static Date parse(String dateStr) throws ParseException {
        return parse(dateStr, pattern);
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getDateNow() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 根据格式   获取当前时间
     *
     * @return
     */
    public static String getDateNow(String pattern) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 将date类型转成String类型数据
     *
     * @param date
     * @return
     */
    public static String getDateFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static String getDateFormat(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static Date parse(String dateStr, String pattern) {
        Date date = null;
        try {
            if (StrKit.notBlank(dateStr, pattern)) {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                date = sdf.parse(dateStr);
            }
        } catch (Exception e) {
            logger.error("获取最新的日期出错：", e);
        }
        return date;
    }

    /**
     * 获取当前日期信息
     *
     * @return 字符串类型
     */
    public static String getDateStr() {
        String date = null;
        try {
            date = Db.queryStr("SELECT DATE_FORMAT(NOW(),'%Y-%m-%d %H:%i:%s')");
        } catch (Exception e) {
            logger.error("获取最新的日期出错：", e);
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 将timestamp转成指定的格式数据
     *
     * @param tt
     * @return
     */
    public static String timestampToDate(Timestamp tt, String pattern) {
        Date date = new Date(tt.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 获取当天最后的时间  2016-01-11 23:59:59
     *
     * @param date
     * @return
     */
    public static Date getDayEnd(Date date) {
        if (date == null) {
            return null;
        }
        DateTime dt = new DateTime(date);
        dt = new DateTime(dt.year().get(), dt.monthOfYear().get(), dt.getDayOfMonth(), 0, 0, 0);
        return dt.plusDays(1).minusSeconds(1).toDate();
    }

    /**
     * 获取当天开始时间
     *
     * @param date 2016-01-11 00:00:00
     * @return
     */
    public static Date getDayStart(Date date) {
        if (date == null) return null;
        DateTime dt = new DateTime(date);
        return new DateTime(dt.year().get(), dt.monthOfYear().get(), dt.getDayOfMonth(), 0, 0, 0).toDate();
    }

    /**
     * 获取当月第一天
     *
     * @param date 2016-01-01 00:00:00
     * @return
     */
    public static Date getMonthStart(Date date) {
        if (date == null) return null;
        DateTime dt = new DateTime(date);
        return new DateTime(dt.year().get(), dt.monthOfYear().get(), 1, 0, 0, 0).toDate();
    }

    /**
     * 将日期转换成大写的月份
     *
     * @param tt
     * @return
     */
    public static String[] timestampToMonth(Timestamp tt) {
        //Date date= new Date(tt.getTime());
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(tt.getTime());
        //int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);
        String[] arr = {DAXIE_MONTH[month - 1] + "月", String.valueOf(d)};
        return arr;
    }

    /**
     * 获取本周第一天(星期一)
     *
     * @param date
     * @return
     */
    public static Date getWeekStart(Date date) {
        if (date == null) return null;
        date = getDayStart(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week == 0) {
            week = 7;
        }

        cal.add(Calendar.DATE, (1 - week));

        return cal.getTime();
    }

    public static Date dateAdd(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    public static Date monthAdd(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }
}
