package com.xueduoduo.health.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.github.java.common.base.BaseResp;
import com.github.java.common.utils.JavaAssert;
import com.xueduoduo.health.controller.dto.StudentQuestionnairReq;
import com.xueduoduo.health.domain.common.HealthException;
import com.xueduoduo.health.domain.enums.ReturnCode;
import com.xueduoduo.health.domain.service.QuestionnaireService;
import com.xueduoduo.health.domain.user.User;
import com.xueduoduo.health.domain.user.UserRepository;
import com.xueduoduo.health.login.UserSessionUtils;

/**
 * 学生操作
 * 
 * @author wangzhifeng
 * @date 2018年8月15日 下午3:29:34
 */
@RestController
public class StudentController {

    private static final Logger  logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private QuestionnaireService questionnaireService;
    @Autowired
    private UserRepository userRepository;
    
    public BaseResp studentCenter(@RequestBody StudentQuestionnairReq req){
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            logger.info("展示学生个人中心,req :{}", req);
            User u = userRepository.loadUserWithPasswdById(req.getStudentId());
            resp.setData(u);
        } catch (Exception e) {
            logger.error("展示学生个人中心异常", e);
            resp = BaseResp.buildFailResp("展示学生个人中心结果异常", BaseResp.class);
        }
        logger.info("展示学生个人中心,resp:{}", resp);
        return resp;
    }

    /**
     * 展示学生测评问卷列表
     */
    @RequestMapping(value = "student/questionnaireList", method = RequestMethod.GET)
    public BaseResp questionnaireList(@RequestBody StudentQuestionnairReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            logger.info("展示学生测评问卷列表,req :{}", req);
            JavaAssert.isTrue(null != req.getStudentId(), ReturnCode.PARAM_ILLEGLE, "学生Id不能为空", HealthException.class);

            JSONObject json = questionnaireService.loadStudentUserQuestionnaires(req.getStudentId());
            resp.setData(json);
        } catch (Exception e) {
            logger.error("保存教师测评学生结果异常", e);
            resp = BaseResp.buildFailResp("保存教师测评学生结果异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("展示学生测评问卷列表,resp:{}", resp);
        return resp;
    }

    /**
     * 学生开始测评第一题
     */
    @RequestMapping(value = "student/startStudentTestQuestionnaire", method = RequestMethod.GET)
    public BaseResp startStudentTestQuestionnaire(@RequestBody StudentQuestionnairReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            logger.info("展示学生测评问卷列表,req :{}", req);

            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            JavaAssert.isTrue(null != req.getStudentId(), ReturnCode.PARAM_ILLEGLE, "学生Id不能为空", HealthException.class);

            return questionnaireService.startStudentTestQuestionnaire(req.getQuestionnaireId(), req.getStudentId());
        } catch (Exception e) {
            logger.error("学生开始测评第一题异常", e);
            resp = BaseResp.buildFailResp("学生开始测评第一题异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("展示学生开始测评第一题结果,resp:{}", resp);
        return resp;
    }

    /**
     * 保存学生测评结果,并返回需要的题目信息
     */
    @RequestMapping(value = "student/addStudentTestQuestionnaire", method = RequestMethod.POST)
    public BaseResp addStudentTestQuestionnaire(@RequestBody StudentQuestionnairReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            logger.info("展示学生测评问卷列表,req :{}", req);

            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            JavaAssert.isTrue(null != req.getStudentId(), ReturnCode.PARAM_ILLEGLE, "学生Id不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getQuestionId(), ReturnCode.PARAM_ILLEGLE, "学生测评题目不能为空",
                    HealthException.class);
            JavaAssert.isTrue(null != req.getOptionId(), ReturnCode.PARAM_ILLEGLE, "学生测评答案选项不能为空",
                    HealthException.class);

            JavaAssert.isTrue(null == req.getNextQuestionId() && null == req.getPreQuestionId(),
                    ReturnCode.PARAM_ILLEGLE, "学生测评操作不能为空", HealthException.class);

            User user = UserSessionUtils.getUserFromSession();
            Long questionnaireId = req.getQuestionnaireId();
            //保存学生答案
            questionnaireService.saveStudentTestQuestionnaire(questionnaireId, req.getStudentId(), req.getQuestionId(),
                    req.getOptionId(), req.getAnswerId());

            //展示学生选择的下一个题目
            resp = questionnaireService.showUserNextQuestion(questionnaireId, req.getNextQuestionId(),
                    req.getPreQuestionId(), req.getStudentId());
        } catch (HealthException e) {
            logger.error("保存学生回答问卷结果异常", e);
            resp = BaseResp.buildFailResp("保存学生回答问卷结果异常" + e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("保存学生回答问卷结果异常", e);
            resp = BaseResp.buildFailResp("保存学生回答问卷结果异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("保存学生回答问卷结果,resp:{}", resp);
        return resp;
    }

    /**
     * 学生交卷
     */
    @RequestMapping(value = "student/studentSubmit", method = RequestMethod.POST)
    public BaseResp studentSubmit(@RequestBody StudentQuestionnairReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            logger.info("学生交卷,req :{}", req);
            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            JavaAssert.isTrue(null != req.getStudentId(), ReturnCode.PARAM_ILLEGLE, "学生Id不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getQuestionId(), ReturnCode.PARAM_ILLEGLE, "学生测评题目不能为空",
                    HealthException.class);
            JavaAssert.isTrue(null != req.getOptionId(), ReturnCode.PARAM_ILLEGLE, "学生测评答案选项不能为空",
                    HealthException.class);

            User user = UserSessionUtils.getUserFromSession();
            Long questionnaireId = req.getQuestionnaireId();
            //保存学生答案
            resp = questionnaireService.studentSumbit(questionnaireId, req.getStudentId(), req.getQuestionnaireId(),
                    req.getOptionId(), req.getAnswerId());
        } catch (HealthException e) {
            logger.error("保存学生回答问卷结果异常", e);
            resp = BaseResp.buildFailResp("保存学生回答问卷结果异常" + e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("保存学生回答问卷结果异常", e);
            resp = BaseResp.buildFailResp("保存学生回答问卷结果异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("学生交卷,resp:{}", resp);

        return resp;
    }
}
