package com.xueduoduo.health.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.java.common.base.BaseResp;
import com.github.java.common.base.Page;
import com.github.java.common.utils.JavaAssert;
import com.github.java.common.utils.StringUtils;
import com.xueduoduo.health.configuration.SchoolYearUtils;
import com.xueduoduo.health.domain.common.HealthException;
import com.xueduoduo.health.domain.enums.IsDeleted;
import com.xueduoduo.health.domain.enums.ReturnCode;
import com.xueduoduo.health.domain.latitude.Latitude;
import com.xueduoduo.health.domain.latitude.LatitudeRepository;

/**
 * @author wangzhifeng
 * @date 2018年8月12日 上午12:28:33
 */
@RestController
public class LatitudeController {
    private static final Logger logger = LoggerFactory.getLogger(LatitudeController.class);

    @Autowired
    private LatitudeRepository  latitudeRepository;
    @Autowired
    private SchoolYearUtils     schoolYearUtils;

    /**
     * 纬度列表
     */
    @RequestMapping(value = "admin/review/latitudeList", method = RequestMethod.POST)
    public BaseResp latitudeList(HttpServletRequest req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            String sy = (String) req.getParameter("schoolYear");
            String name = (String) req.getParameter("displayName");
            String offSetStr = (String) req.getParameter("offSet");
            String lengthStr = (String) req.getParameter("length");
            Page page = latitudeRepository.loadPage(sy, name, offSetStr, lengthStr);
            resp.setData(page);
        } catch (Exception e) {
            logger.error("查询纬度异常", e);
            resp = BaseResp.buildFailResp("查询纬度异常", BaseResp.class);
        }
        return resp;
    }

    /**
     * 添加纬度
     */
    @RequestMapping(value = "admin/review/addLatitude", method = RequestMethod.GET)
    public BaseResp addLatitude(HttpServletRequest req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            String name = (String) req.getParameter("displayName");
            JavaAssert.isTrue(StringUtils.isNotBlank(name), ReturnCode.PARAM_ILLEGLE, "纬度名不能为空", HealthException.class);
            String year = schoolYearUtils.getCurrentSchoolYear();
            latitudeRepository.save(name, year);
        } catch (Exception e) {
            logger.error("添加纬度异常", e);
            resp = BaseResp.buildFailResp("添加纬度异常" + e.getMessage(), BaseResp.class);
        }
        return resp;
    }

    /**
     * 编辑纬度
     */
    @RequestMapping(value = "admin/review/editLatitude", method = RequestMethod.GET)
    public BaseResp editLatitude(HttpServletRequest req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            String name = (String) req.getParameter("displayName");
            String idStr = req.getParameter("id");

            JavaAssert.isTrue(StringUtils.isNotBlank(idStr), ReturnCode.PARAM_ILLEGLE, "纬度ID不能为空",
                    HealthException.class);
            JavaAssert.isTrue(StringUtils.isNotBlank(name), ReturnCode.PARAM_ILLEGLE, "纬度名不能为空", HealthException.class);
            Latitude src = new Latitude();
            src.setId(Long.parseLong(idStr));
            src.setDisplayName(name);
            latitudeRepository.update(src);
        } catch (Exception e) {
            logger.error("编辑纬度异常", e);
            resp = BaseResp.buildFailResp("编辑纬度异常" + e.getMessage(), BaseResp.class);
        }
        return resp;
    }

    /**
     * 删除纬度
     */
    @RequestMapping(value = "admin/review/deleteLatitude", method = RequestMethod.GET)
    public BaseResp deleteLatitude(HttpServletRequest req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            String idStr = req.getParameter("id");

            JavaAssert.isTrue(StringUtils.isNotBlank(idStr), ReturnCode.PARAM_ILLEGLE, "纬度ID不能为空",
                    HealthException.class);
            Latitude src = new Latitude();
            src.setId(Long.parseLong(idStr));
            src.setIsDeleted(IsDeleted.Y.name());
            latitudeRepository.update(src);
        } catch (Exception e) {
            logger.error("删除纬度异常", e);
            resp = BaseResp.buildFailResp("删除纬度异常" + e.getMessage(), BaseResp.class);
        }
        return resp;
    }
}
