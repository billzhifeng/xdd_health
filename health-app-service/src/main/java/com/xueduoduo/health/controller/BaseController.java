package com.xueduoduo.health.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.github.java.common.utils.DateUtil;
import com.xueduoduo.health.configuration.SchoolYearUtils;

/**
 * 基础
 * 
 * @author wangzhifeng
 */
@RestController
public class BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);
    @Autowired
    private SchoolYearUtils     schoolYearUtils;

    @RequestMapping(value = "/ok", method = RequestMethod.GET)
    @ResponseBody
    public String sayOK() {
        JSONObject json = new JSONObject();
        try {
            json.put("OK", "OK");
        } catch (Exception e) {

        }
        return json.toJSONString();
    }

    @RequestMapping(value = "/admin/review/latestSchoolYears", method = RequestMethod.GET)
    @ResponseBody
    public String getLatestSchoolYear() {
        JSONObject json = new JSONObject();
        String CurrentSchoolYear = schoolYearUtils.getCurrentSchoolYear();
        json.put("CurrentSchoolYear", CurrentSchoolYear);
        json.put("CurrentSchoolYearStartDate",
                DateUtil.formatDate(schoolYearUtils.getCurrentSchoolYearStartDate(), DateUtil.webFormat));
        json.put("CurrentSchoolYearEndDate",
                DateUtil.formatDate(schoolYearUtils.getCurrentSchoolYearEndDate(), DateUtil.webFormat));
        json.put("latestSchoolYear", schoolYearUtils.getLatestSchoolYear());
        return json.toJSONString();
    }

}
