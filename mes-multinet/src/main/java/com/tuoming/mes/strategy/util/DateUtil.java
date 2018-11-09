package com.tuoming.mes.strategy.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.tuoming.mes.strategy.consts.Constant;

public class DateUtil {

    /**
     * 获取指定时间间隔的时刻表
     *
     * @param nextDay
     * @param beginHour      开始时间
     * @param endHour        结束时间
     * @param minuteInterval 时间间隔
     * @return
     */
    public static List<Date> getTimeList(Date nextDay, int beginHour,
                                         int endHour, int minuteInterval) {
        List<Date> resList = new ArrayList<>();
        Calendar beginCal = Calendar.getInstance();
        beginCal.setTime(nextDay);
        beginCal.set(Calendar.HOUR_OF_DAY, beginHour);
        beginCal.set(Calendar.MINUTE, 0);
        beginCal.set(Calendar.SECOND, 0);
        beginCal.set(Calendar.MILLISECOND, 0);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(nextDay);
        endCal.set(Calendar.HOUR_OF_DAY, endHour);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.MILLISECOND, 0);
        while (beginCal.before(endCal)) {
            resList.add(beginCal.getTime());
            beginCal.add(Calendar.MINUTE, minuteInterval);
        }
        return resList;
    }

    /**
     * 获取当前时间的下一天
     *
     * @return
     */
    public static Date getNextDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /**
     * 根据传入的星期数，查询前weekNum星期每个当前星期的日期
     *
     * @param WeekNum
     * @return List<Date>
     */
    public static List<Date> getHistoryDate(Date d, int WeekNum) {
        List<Date> ld = new ArrayList<Date>();
        for (int g = 1; g <= WeekNum; g++) {
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            c.add(Calendar.DATE, -(g * 7));
            ld.add(c.getTime());
        }
        return ld;
    }

    /**
     * 判断是否满足4周到8周
     *
     * @param bd
     * @param ad
     * @return boolean
     * @throws ParseException
     */
    public static boolean validateInSecondMonth(Date bd, Date ad) {
        long diff = bd.getTime() - ad.getTime();
        long days = Math.abs(diff / (1000 * 60 * 60 * 24));
        if (days >= 29 && days < 56) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否满足4周
     *
     * @param bd
     * @param ad
     * @return boolean
     * @throws ParseException
     */
    public static boolean validateInFirstMonth(Date bd, Date ad) {
        long diff = bd.getTime() - ad.getTime();
        long days = Math.abs(diff / (1000 * 60 * 60 * 24));
        if (days <= 28) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将日期转换为年-月-日 时:分:秒格式
     *
     * @param foreCastDate
     * @return
     */
    public static String format(Date foreCastDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(foreCastDate);
    }

    /**
     * 把字符串转换为日期
     *
     * @param str
     * @return
     */
    public static Date tranStrToDate(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取相对于指定日期几天之前或几天之后的日期集合
     *
     * @param foreCastDate
     * @param days
     * @return
     */
    public static List<Date> getRelateDays(Date foreCastDate, int days) {
        List<Date> timeList = new ArrayList<Date>();
        if (days > 0) {
            while (days > 0) {
                Calendar c = Calendar.getInstance();
                c.setTime(foreCastDate);
                c.add(Calendar.DATE, days);
                timeList.add(c.getTime());
                days--;
            }
        } else {
            while (days < 0) {
                Calendar c = Calendar.getInstance();
                c.setTime(foreCastDate);
                c.add(Calendar.DATE, days);
                timeList.add(c.getTime());
                days++;
            }
        }
        return timeList;
    }

    /**
     * 获取相对于指定时间，指定间隔的日期
     */
    public static Date getRelateInterval(Date date, int inverval, int unit) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(unit, inverval);
        return c.getTime();
    }

    /**
     * 获取日期的前一天
     *
     * @return
     */
    public static Date getBeforeDay(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }

    /**
     * 获取时间的前一小时
     *
     * @return
     */
    public static Date getBeforeHour(Date d) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(Calendar.HOUR, -1);
        return calendar.getTime();
    }

    public static String format(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 获取当前时刻多少天以前或以后的零点天日期
     *
     * @param days
     * @return
     */
    public static String getDay(int days) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, days);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return sdf.format(c.getTime());
    }

    /**
     * @param begin
     * @param end
     * @param pmCollectLd
     * @return
     */
    public static List<Date> getMinInterval(Calendar begin, Calendar end,
                                            int pmCollectLd) {
        List<Date> dateList = new ArrayList<Date>();
        while (begin.before(end)) {
            dateList.add(begin.getTime());
            begin.add(Calendar.MINUTE, pmCollectLd);
            ;
        }
        return dateList;
    }

    /**
     * 获取当前时间 格式为yyyy-MM-dd HH:mm:ss
     *
     * @return 当前时间字符串
     */
    public static String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        String queryDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                cal.getTime()).toString();
        return queryDate;
    }

    /**
     * 计算一段时间固定间隔的时间列表
     *
     * @param beginDay  开始日期
     * @param endDay    结束日期
     * @param beginHour 开始时间
     * @param endHour   结束时间
     * @param interval  时间间隔
     * @return
     */
    public static List<String> getIntervalList(String beginDay, String endDay, int beginHour, int endHour, int interval) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<String> resList = new ArrayList<String>();
        Calendar beginCal = Calendar.getInstance();
        beginCal.setTime(DateUtil.tranStrToDate(beginDay));
        beginCal.set(Calendar.HOUR_OF_DAY, beginHour);
        beginCal.set(Calendar.MINUTE, 0);
        beginCal.set(Calendar.SECOND, 0);
        beginCal.set(Calendar.MILLISECOND, 0);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(DateUtil.tranStrToDate(endDay));
        endCal.set(Calendar.HOUR_OF_DAY, endHour);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.MILLISECOND, 0);
        while (beginCal.before(endCal)) {
            resList.add(df.format(beginCal.getTime()));
            beginCal.add(Calendar.MINUTE, interval);
            ;
        }
        return resList;
    }

    /**
     * 某时间的前15min日期
     *
     * @param d
     * @return
     */
    public static String getBeforeMin(Date d, int min) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(Calendar.MINUTE, -min);
        return df.format(calendar.getTime()).substring(12);
    }


    /**
     * 判断时间是否>=6h
     *
     * @param timeRange
     * @return
     */
    public static List<Map<String, String>> exceed6Hour(List<Map<String, String>> timeRange) {
        List<Map<String, String>> res = new ArrayList<Map<String, String>>();
        Date nowDate = new Date();
        Calendar beginCal = Calendar.getInstance();
        beginCal.setTime(nowDate);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(nowDate);

        for (Map<String, String> map : timeRange) {
            beginCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(map.get("stime").substring(0, 2)));
            beginCal.set(Calendar.MINUTE, Integer.parseInt(map.get("stime").substring(3, 5)));
            beginCal.set(Calendar.SECOND, 0);
            beginCal.set(Calendar.MILLISECOND, 0);
            endCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(map.get("etime").substring(0, 2)));
            endCal.set(Calendar.MINUTE, Integer.parseInt(map.get("etime").substring(3, 5)));
            endCal.set(Calendar.SECOND, 0);
            endCal.set(Calendar.MILLISECOND, 0);
            //开始时间大于结束时间说明出现了跨天数据
            if (beginCal.after(endCal)) {
                //结束时间减一天
                beginCal.set(Calendar.DAY_OF_MONTH, -1);
            }
            Date bDate = beginCal.getTime();
            Date eDate = endCal.getTime();
            long diff = eDate.getTime() - bDate.getTime();
            long hours = diff / (1000 * 60 * 60);
            if (hours >= 6) {
                res.add(map);
            }
        }
        return res;
    }

    /**
     * 固定时段间隔指定时间的时间集合
     *
     * @param beginHour HH:mm:ss  开始时间
     * @param endHour   HH:mm:ss 结束时间
     * @param interval  时间间隔
     * @return
     */
    public static List<String> getIntervalTime(String beginTime, String endTime, int interval) {
        if (null == beginTime) {
            beginTime = "00:00:00";
        }
        if (null == endTime) {
            endTime = "23:59:59";
        }
        int beginHour = Integer.parseInt(beginTime.split(":")[0]);
        int beginMin = Integer.parseInt(beginTime.split(":")[1]);
        int endHour = Integer.parseInt(endTime.split(":")[0]);
        int endMin = Integer.parseInt(endTime.split(":")[1]);
        Date nowDate = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<String> resList = new ArrayList<String>();
        Calendar beginCal = Calendar.getInstance();
        beginCal.setTime(nowDate);
        beginCal.set(Calendar.HOUR_OF_DAY, beginHour);
        beginCal.set(Calendar.MINUTE, beginMin);
        beginCal.set(Calendar.SECOND, 0);
        beginCal.set(Calendar.MILLISECOND, 0);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(nowDate);
        endCal.set(Calendar.HOUR_OF_DAY, endHour);
        endCal.set(Calendar.MINUTE, endMin);
        endCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.MILLISECOND, 0);
        while (beginCal.before(endCal)) {
            resList.add(df.format(beginCal.getTime()).substring(11));
            beginCal.add(Calendar.MINUTE, interval);
            ;
        }
        return resList;
    }

    /**
     * 获取相对于指定日期几天之前日期列表
     *
     * @param startDay yyyy-MM-dd HH:mm:ss 起始日期
     * @param days     天数
     * @return
     */
    public static List<String> getRelateDays(String startDay, int days) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        List<String> timeList = new ArrayList<String>();
        //Date bd = DateUtil.tranStrToDate(foreCastDate +" 00:00:00");
        Date bd = DateUtil.tranStrToDate(startDay);
        while (days > 0) {
            Calendar c = Calendar.getInstance();
            c.setTime(bd);
            c.add(Calendar.DATE, -days);
            timeList.add(df.format(c.getTime()));
            days--;
        }
        return timeList;
    }

    /**
     * 某时间的前15min时间
     *
     * @param d
     * @return
     */
    public static String getBeforeMinStr(Date d, int min) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(Calendar.MINUTE, -min);
        return df.format(calendar.getTime());
    }

    /**
     * 某时间的前15min时间
     *
     * @param d
     * @param min
     * @return
     */
    public static Date getBeforeMinDate(Date d, int min) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(Calendar.MINUTE, -min);
        return calendar.getTime();
    }

    /**
     * 获得当前属于哪个15min时段
     *
     * @param nowDate
     * @param timeStr
     * @return HH:mm:SS
     */
    public static String getMultiple15Min(Date nowDate) {
        String before15Min = DateUtil.getBeforeMinStr(nowDate, 15);
        String beforeTime = before15Min.substring(11);
        String min = before15Min.substring(14, 16);
        String nowMin = "";
        if (Integer.parseInt(min) >= 0 && Integer.parseInt(min) < 15) {
            nowMin = "00";
        } else if (Integer.parseInt(min) >= 15 && Integer.parseInt(min) < 30) {
            nowMin = "15";
        } else if (Integer.parseInt(min) >= 30 && Integer.parseInt(min) < 45) {
            nowMin = "30";
        } else if (Integer.parseInt(min) >= 45) {
            nowMin = "45";
        }
        return beforeTime.substring(0, 3) + nowMin + ":00";
    }

    /**
     * 获得指定时间的前或后几天时间
     */
    public static Date getDelayDay(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return c.getTime();
    }


    public static void main(String args[]) throws ParseException {
            List<Date> resultList = new ArrayList<>();
            // 当前天的预测时间
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.SECOND, 0);// 采集时间不需要秒信息
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.MINUTE,
                    (cal.get(Calendar.MINUTE) / Constant.PM_COLLECT_LD)
                            * Constant.PM_COLLECT_LD);
            Date nowBefore15Min = DateUtil.tranStrToDate(DateUtil.getBeforeMinStr(
                    cal.getTime(), 15));
//                    new SimpleDateFormat("yyyy-MM-dd hh:mm").parse("2018-11-02 03:15");


            Date sqlTime =   new SimpleDateFormat("yyyy-MM-dd hh:mm").parse("2018-11-02 06:15");
            // HH:mm:ss
            if (null == sqlTime || "".equals(sqlTime)) {
                if (nowBefore15Min.getHours() >= 0 && nowBefore15Min.getHours() <= 6) {
                    System.out.println(">>>>>>>>>>>>>>>>>>" + nowBefore15Min.getHours());
                    resultList = DateUtil.getTimeList(new Date(), nowBefore15Min.getHours(), 7, 15);
                } else {
                    String befortime = DateUtil.getDay(1);//获取下一天0点开始到6点的数据
                    resultList = DateUtil.getTimeList(DateUtil.tranStrToDate(befortime), 0, 7, 15);
                }
            } else {
                // 数据库预测的最后时间转为预测时间
                Date sqlBeforeDate = DateUtil.getBeforeDay(sqlTime);
                while (sqlBeforeDate.before(nowBefore15Min)) {
                    // 当前时间再取前15min
                    sqlBeforeDate = DateUtil.tranStrToDate(DateUtil.getBeforeMinStr(sqlBeforeDate, -15));
                    if (sqlBeforeDate.getHours() >= 0 && sqlBeforeDate.getHours() <= 6) {
                        resultList.add(sqlBeforeDate);
                    }
                }
            }

        for (int i = 0; i < resultList.size(); i++) {
            System.out.println(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(resultList.get(i)));
        }
        }
}
