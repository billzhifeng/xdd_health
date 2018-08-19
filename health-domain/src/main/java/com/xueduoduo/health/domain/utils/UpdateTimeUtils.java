package com.xueduoduo.health.domain.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author wangzhifeng
 * @date 2018年8月18日 下午2:10:41
 */
public class UpdateTimeUtils {

    /**
     * 获取更新时间显示
     * 
     * @param updateTime
     * @return
     */
    public static String getUpdateTimeStr(Date updateTime) {
        Date now = new Date();
        String display = "";

        Calendar sysDate = new GregorianCalendar();
        sysDate.setTime(now);
        Calendar failDate = new GregorianCalendar();
        failDate.setTime(updateTime);

        long seconds = (sysDate.getTimeInMillis() - failDate.getTimeInMillis()) / 1000;
        display = seconds + "秒前";
        if (seconds / 60 > 0 && seconds / 60 < 60) { //超过一分钟 小于一个小时
            display = seconds / 60 + "分钟前";
        } else if (seconds > 3600 && (seconds / 3600) < 24) {//超过一个小时 但小于一天
            display = seconds / (60 * 60) + "小时前";
        } else if ((seconds / 3600) > 24) {
            display = (seconds / 3600 / 24) + "天前";
        }
        return display;
    }

}
