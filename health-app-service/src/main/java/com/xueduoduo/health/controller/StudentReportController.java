package com.xueduoduo.health.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.github.java.common.base.BaseResp;
import com.github.java.common.utils.JavaAssert;
import com.xueduoduo.health.controller.dto.StudentReportReq;
import com.xueduoduo.health.domain.common.HealthException;
import com.xueduoduo.health.domain.enums.ReturnCode;
import com.xueduoduo.health.domain.enums.UserRoleType;
import com.xueduoduo.health.domain.questionnaire.repository.UserQuestionnaireRepository;
import com.xueduoduo.health.domain.user.User;
import com.xueduoduo.health.domain.user.UserRepository;

/**
 * 学生档案
 * 
 * @author wangzhifeng
 * @date 2018年8月19日 下午7:10:36
 */
public class StudentReportController {
    private static final Logger logger = LoggerFactory.getLogger(StudentReportController.class);

    @Autowired
    private UserRepository      userRepository;
    @Autowired
    private UserQuestionnaireRepository userQuestionnaireRepository;

    /**
     * 学生档案列表
     */
    @RequestMapping(value = "admin/student/studentList", method = RequestMethod.POST)
    public BaseResp studentList(@RequestBody StudentReportReq req) {
        logger.info("收到学生档案列表查询 req:{}", req);
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);

            List<User> users = userRepository.loadUser(req.getGradeNo(), req.getClassNo(), req.getUserName(),
                    req.getOffSet(), req.getLength(), UserRoleType.STUDENT.name());
            resp.setData(users);
        } catch (HealthException e) {
            logger.error("学生档案列表查询异常", e);
            resp = BaseResp.buildFailResp("学生档案列表查询异常", BaseResp.class);
        } catch (Exception e) {
            logger.error("学生档案列表查询异常", e);
            resp = BaseResp.buildFailResp("学生档案列表查询异常", BaseResp.class);
        }
        logger.info("学生档案列表查询返回:{}", resp);
        return resp;
    }

    /**
     * 学生档案图
     */
    @RequestMapping(value = "admin/student/studentScoreChart", method = RequestMethod.POST)
    public BaseResp studentScoreChart(@RequestBody StudentReportReq req) {
        logger.info("收到学生档案图查询 req:{}", req);
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getStudentId(), ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);

            //userAnswer 统计
        } catch (HealthException e) {
            logger.error("学生档案图查询异常", e);
            resp = BaseResp.buildFailResp("学生档案图查询异常", BaseResp.class);
        } catch (Exception e) {
            logger.error("学生档案图查询异常", e);
            resp = BaseResp.buildFailResp("学生档案图查询异常", BaseResp.class);
        }
        logger.info("学生档案图查询返回:{}", resp);
        return resp;
    }
}
