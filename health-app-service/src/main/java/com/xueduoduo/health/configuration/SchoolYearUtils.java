package com.xueduoduo.health.configuration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.java.common.utils.DateUtil;
import com.xueduoduo.health.domain.common.HealthException;
import com.xueduoduo.health.domain.enums.ReturnCode;

/**
 * @author wangzhifeng
 * @date 2018年8月11日 下午8:50:16
 */
@Service
public class SchoolYearUtils {

    //MMdd
    @Value("${schoolYear.start.date}")
    private String startDate    = "0801";

    @Value("${schoolYear.end.date}")
    private String endDate      = "0731";

    //2017-2018中间连接符号-
    @Value("${schoolYear.middle.symbol}")
    private String middleSymbol = "-";

    public static void main(String[] args) {
        SchoolYearUtils schoolYearUtils = new SchoolYearUtils();
        JSONObject json = new JSONObject();
        try {

            String CurrentSchoolYear = schoolYearUtils.getCurrentSchoolYear();
            json.put("CurrentSchoolYear", CurrentSchoolYear);
            json.put("CurrentSchoolYearStartDate",
                    DateUtil.formatDate(schoolYearUtils.getCurrentSchoolYearStartDate(), DateUtil.webFormat));
            json.put("CurrentSchoolYearEndDate",
                    DateUtil.formatDate(schoolYearUtils.getCurrentSchoolYearEndDate(), DateUtil.webFormat));
            json.put("LatestSchoolYear", JSON.toJSONString(schoolYearUtils.getLatestSchoolYear()));

            Date startDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            cal.set(Calendar.YEAR, startDate.getYear() + 1);
            cal.set(Calendar.MONTH, 7);
            Date endDate = cal.getTime();

            json.put("getSchoolYear", schoolYearUtils.getSchoolYear(startDate, endDate, "不在当前学年范围内"));
        } catch (Exception e) {

        }
        System.out.println(json.toJSONString());
    }

    /**
     * 当前时间对于的学年开始时间
     * 
     * @return
     */
    public Date getCurrentSchoolYearStartDate() {

        int month = Integer.parseInt(startDate.substring(0, 2));
        int day = Integer.parseInt(startDate.substring(2, 4));

        Calendar cal = Calendar.getInstance();
        Date startDate = cal.getTime();
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);

        Date today = new Date();
        //当前时间未到学年开始时间 08月01日，学年开始时间是 去年0801
        startDate = cal.getTime();
        if (today.before(startDate)) {
            cal.add(Calendar.YEAR, -1);
        }

        return cal.getTime();
    }

    /**
     * 当前时间对于的学年结束时间
     * 
     * @return
     */
    public Date getCurrentSchoolYearEndDate() {

        int month = Integer.parseInt(endDate.substring(0, 2));
        int day = Integer.parseInt(endDate.substring(2, 4));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MINUTE, 59);

        Date today = new Date();
        //当前时间超过学年结束时间 07月31日，学年结束时间是 下年0731
        if (today.after(cal.getTime())) {
            cal.add(Calendar.YEAR, 1);
        }

        return cal.getTime();
    }

    /**
     * 获得最近的学年,从2018-2019学年开始
     * 
     * @return
     */
    public List<String> getLatestSchoolYear() {
        List<String> list = new ArrayList<String>();
        String current = getCurrentSchoolYear();
        int startYear = 2018;
        String initSchoolYear = startYear + middleSymbol + "2019";

        //只有一个学年
        if (initSchoolYear.equals(current)) {
            list.add(current);
            return list;
        }

        //从2018-2019 开始，到如今的所有学年
        String endSchoolYear = getCurrentSchoolYear();
        int endYear = Integer.parseInt(endSchoolYear.substring(0, 4));
        for (int i = startYear; i <= endYear; i++) {
            int endYearNum = i + 1;
            String schoolYearStr = i + middleSymbol + endYearNum;
            list.add(schoolYearStr);
        }
        return list;
    }

    /**
     * 根据传入时间获得学年 2018-2019
     * 
     * @param date
     * @return
     */
    public String getCurrentSchoolYear() {
        Date start = getCurrentSchoolYearStartDate();
        Date end = getCurrentSchoolYearEndDate();
        int y = start.getYear();
        int startY = start.getYear() + 1900;
        int endY = end.getYear() + 1900;
        return startY + middleSymbol + endY;
    }

    /**
     * 根据传入时间获得学年
     * 
     * @param date
     * @return
     */
    public String getSchoolYear(Date startDate, Date endDate, String msg) {
        Date start = getCurrentSchoolYearStartDate();
        Date end = getCurrentSchoolYearEndDate();
        //在当前学年之间
        if (startDate.after(start) && endDate.before(end)) {
            return getCurrentSchoolYear();
        }
        throw new HealthException(ReturnCode.OPERATOR_DATE_ILLEGLE, msg);
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getMiddleSymbol() {
        return middleSymbol;
    }

    public void setMiddleSymbol(String middleSymbol) {
        this.middleSymbol = middleSymbol;
    }

}
