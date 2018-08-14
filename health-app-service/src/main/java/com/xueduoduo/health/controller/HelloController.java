package com.xueduoduo.health.controller;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.java.common.utils.DateUtil;
import com.xueduoduo.health.configuration.SchoolYearUtils;

/**
 * 测试代码
 * 
 * @author wangzhifeng
 */
@RestController
public class HelloController {

    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);
    @Autowired
    private SchoolYearUtils     schoolYearUtils;

    @RequestMapping(value = "/ok", method = RequestMethod.GET)
    @ResponseBody
    public String sayOK() {
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
        return json.toJSONString();
    }

}
