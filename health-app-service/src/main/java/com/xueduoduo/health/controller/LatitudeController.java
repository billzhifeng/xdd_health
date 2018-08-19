package com.xueduoduo.health.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.java.common.base.BaseResp;
import com.github.java.common.base.Page;
import com.github.java.common.utils.JavaAssert;
import com.github.java.common.utils.StringUtils;
import com.xueduoduo.health.configuration.SchoolYearUtils;
import com.xueduoduo.health.controller.dto.LatitudeReq;
import com.xueduoduo.health.domain.common.HealthException;
import com.xueduoduo.health.domain.enums.IsDeleted;
import com.xueduoduo.health.domain.enums.ReturnCode;
import com.xueduoduo.health.domain.latitude.Latitude;
import com.xueduoduo.health.domain.latitude.LatitudeRepository;
import com.xueduoduo.health.domain.user.User;
import com.xueduoduo.health.login.UserSessionUtils;

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
    public BaseResp latitudeList(@RequestBody LatitudeReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            Page page = latitudeRepository.loadPage(req.getSchoolYear(), req.getDisplayName(), req.getOffSet(),
                    req.getLength());
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
    @RequestMapping(value = "admin/review/addLatitude", method = RequestMethod.POST)
    public BaseResp addLatitude(@RequestBody LatitudeReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            JavaAssert.isTrue(StringUtils.isNotBlank(req.getDisplayName()), ReturnCode.PARAM_ILLEGLE, "纬度名不能为空",
                    HealthException.class);
            String year = schoolYearUtils.getCurrentSchoolYear();
            User user = UserSessionUtils.getUserFromSession();
            latitudeRepository.save(req.getDisplayName(), year, user.getUserName());
        } catch (Exception e) {
            logger.error("添加纬度异常", e);
            resp = BaseResp.buildFailResp("添加纬度异常" + e.getMessage(), BaseResp.class);
        }
        return resp;
    }

    /**
     * 编辑纬度
     */
    @RequestMapping(value = "admin/review/editLatitude", method = RequestMethod.POST)
    public BaseResp editLatitude(@RequestBody LatitudeReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);

            JavaAssert.isTrue(null != req.getId(), ReturnCode.PARAM_ILLEGLE, "纬度ID不能为空", HealthException.class);
            JavaAssert.isTrue(StringUtils.isNotBlank(req.getDisplayName()), ReturnCode.PARAM_ILLEGLE, "纬度名不能为空",
                    HealthException.class);
            Latitude src = new Latitude();
            src.setId(req.getId());
            src.setDisplayName(req.getDisplayName());
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
    @RequestMapping(value = "admin/review/deleteLatitude", method = RequestMethod.POST)
    public BaseResp deleteLatitude(@RequestBody LatitudeReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);

            JavaAssert.isTrue(null != req.getId(), ReturnCode.PARAM_ILLEGLE, "纬度ID不能为空", HealthException.class);
            Latitude src = new Latitude();
            src.setId(req.getId());
            src.setIsDeleted(IsDeleted.Y.name());
            latitudeRepository.updateToDelete(src);
        } catch (Exception e) {
            logger.error("删除纬度异常", e);
            resp = BaseResp.buildFailResp("删除纬度异常" + e.getMessage(), BaseResp.class);
        }
        return resp;
    }
}
