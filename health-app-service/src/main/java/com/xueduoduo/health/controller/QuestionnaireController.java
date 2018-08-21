package com.xueduoduo.health.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.java.common.base.BaseResp;
import com.github.java.common.base.Page;
import com.github.java.common.utils.DateUtil;
import com.github.java.common.utils.JavaAssert;
import com.github.java.common.utils.StringUtils;
import com.xueduoduo.health.configuration.SchoolYearUtils;
import com.xueduoduo.health.controller.dto.AddQuesLatitudeDescReq;
import com.xueduoduo.health.controller.dto.AddQuestionnaireQuestionsInfo;
import com.xueduoduo.health.controller.dto.QuestionnaireReq;
import com.xueduoduo.health.domain.common.HealthException;
import com.xueduoduo.health.domain.enums.QuestionnaireType;
import com.xueduoduo.health.domain.enums.ReturnCode;
import com.xueduoduo.health.domain.questionnaire.Questionnaire;
import com.xueduoduo.health.domain.questionnaire.repository.QuestionnaireRepository;
import com.xueduoduo.health.domain.questionnaire.repository.UserQuestionnaireRepository;
import com.xueduoduo.health.domain.service.QuestionnaireService;
import com.xueduoduo.health.domain.user.User;
import com.xueduoduo.health.login.UserSessionUtils;

/**
 * 问卷、问题、选项、分数
 * 
 * @author wangzhifeng
 * @date 2018年8月12日 下午10:49:38
 */
@RestController
public class QuestionnaireController {

    private static final Logger         logger = LoggerFactory.getLogger(QuestionnaireController.class);

    @Autowired
    private QuestionnaireRepository     questionnaireRepository;

    @Autowired
    private QuestionnaireService        questionnaireService;

    @Autowired
    private SchoolYearUtils             schoolYearUtils;
    @Autowired
    private UserQuestionnaireRepository userQuestionnaireRepository;

    /**
     * 问卷列表
     */
    @RequestMapping(value = "admin/review/questionnaireList", method = RequestMethod.POST)
    public BaseResp questionnaireList(@RequestBody QuestionnaireReq req) {
        logger.info("收到问卷列表查询 req:{}", req);
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            Page<Questionnaire> page = questionnaireRepository.loadPage(req.getSchoolYear(), req.getTitle(),
                    req.getOffSet(), req.getLength());
            resp.setData(page);
        } catch (HealthException e) {
            logger.error("问卷列表查询异常", e);
            resp = BaseResp.buildFailResp("问卷列表查询异常", BaseResp.class);
        } catch (Exception e) {
            logger.error("问卷列表查询异常", e);
            resp = BaseResp.buildFailResp("问卷列表查询异常", BaseResp.class);
        }
        logger.info("问卷列表查询返回:{}", resp);
        return resp;
    }

    /**
     * 添加问卷
     */
    @RequestMapping(value = "admin/review/addQuestionnaire", method = RequestMethod.POST)
    public BaseResp addQuestionnaire(@RequestBody QuestionnaireReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            JavaAssert.isTrue(StringUtils.isNotBlank(req.getQuestionnaireType()), ReturnCode.PARAM_ILLEGLE, "问卷类型不能为空",
                    HealthException.class);
            logger.info("添加问卷请求,questionnaireType:{}", req.getQuestionnaireType());
            User user = UserSessionUtils.getUserFromSession();
            Long id = questionnaireRepository.save(user.getUserName(), req.getQuestionnaireType());
            resp.setData(id);
        } catch (Exception e) {
            logger.error("添加问卷异常", e);
            resp = BaseResp.buildFailResp("添加问卷异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("添加问卷返回:{}", resp);
        return resp;
    }

    /**
     * 复制问卷
     */
    @RequestMapping(value = "admin/review/copyQuestionnaire", method = RequestMethod.POST)
    public BaseResp copyQuestionnaire(@RequestBody QuestionnaireReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            Long id = req.getQuestionnaireId();//req.getParameter("questionnaireId");
            JavaAssert.isTrue(null != id, ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空", HealthException.class);
            logger.info("复制问卷请求,questionnaireId:{}", id);
            User user = UserSessionUtils.getUserFromSession();

            Long newId = questionnaireService.copyQuestionnaireCasecade(id, user.getUserName());
            resp.setData(newId);
        } catch (HealthException e) {
            logger.error("复制问卷异常", e);
            resp = BaseResp.buildFailResp("复制问卷异常" + e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("复制问卷异常", e);
            resp = BaseResp.buildFailResp("复制问卷异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("复制问卷结果,resp:{}", resp);
        return resp;
    }

    /**
     * 展示添加编辑问卷题目+选项
     */
    @RequestMapping(value = "admin/review/showAddQuestionnaireQuestions", method = RequestMethod.POST)
    public BaseResp showAddQuestionnaireQuestions(@RequestBody QuestionnaireReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            //TODO 加图片
            logger.info("展示添加编辑问卷题目选项,请求questionnaireId:{}", req.getQuestionnaireId());
            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            resp = questionnaireService.showAddQuestionnaireQuestions(req.getQuestionnaireId());
        } catch (Exception e) {
            logger.error("查询问卷题目异常", e);
            resp = BaseResp.buildFailResp("查询问卷题目异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("查询问卷题目选项结果,resp:{}", resp);
        return resp;
    }

    /**
     * 保存问卷题目+选项
     */
    @RequestMapping(value = "admin/review/addQuestionnaireQuestions", method = RequestMethod.POST)
    public BaseResp addQuestionnaireQuestions(@RequestBody AddQuestionnaireQuestionsInfo req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            //TODO 加图片
            logger.info("保存问卷题目+选项,请求req:{}", req);
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求为空", HealthException.class);
            JavaAssert.isTrue(StringUtils.isNotBlank(req.getTitle()), ReturnCode.PARAM_ILLEGLE, "问卷标题不能为空",
                    HealthException.class);
            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            JavaAssert.isTrue(StringUtils.isNotBlank(req.getIntroduction()), ReturnCode.PARAM_ILLEGLE, "问卷导语不能为空",
                    HealthException.class);

            User user = UserSessionUtils.getUserFromSession();
            questionnaireService.addQuestionnaireQuestions(req.getQuestionnaireId(), req.getTitle(),
                    req.getIntroduction(), req.getQuestions(), user.getUserName());
            resp.setData(req.getQuestionnaireId());
        } catch (HealthException e) {
            logger.error("保存问卷题目异常", e);
            resp = BaseResp.buildFailResp(e.getReturnMsg() + ",已发布问卷不能编辑", BaseResp.class);
        } catch (Exception e) {
            logger.error("保存问卷题目异常", e);
            resp = BaseResp.buildFailResp("保存问卷题目异常", BaseResp.class);
        }
        logger.info("保存问卷题目+选项,resp:{}", resp);
        return resp;
    }

    /**
     * 发布问卷
     */
    @RequestMapping(value = "admin/review/publishQuestionnaire", method = RequestMethod.POST)
    public BaseResp publishQuestionnaire(@RequestBody QuestionnaireReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);

            logger.info("发布问卷,req :{}", req);

            JavaAssert.isTrue(StringUtils.isNotBlank(req.getEndedDate()), ReturnCode.PARAM_ILLEGLE, "问卷结束时间不能为空",
                    HealthException.class);
            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            JavaAssert.isTrue(req.getGradeNo() > -1, ReturnCode.PARAM_ILLEGLE, "问卷对应年级不能为空", HealthException.class);
            JavaAssert.isTrue(StringUtils.isNotBlank(req.getStartDate()), ReturnCode.PARAM_ILLEGLE, "问卷开始时间不能为空",
                    HealthException.class);

            Date startDate = DateUtil.parseDate(req.getStartDate(), DateUtil.shortFormat);
            Date endedDate = DateUtil.parseDate(req.getEndedDate(), DateUtil.shortFormat);
            String schoolYear = schoolYearUtils.getSchoolYear(startDate, endedDate,
                    "问卷选择的时间,不在同一学年区间内,当前学年:" + schoolYearUtils.getCurrentSchoolYear());

            Questionnaire q = new Questionnaire();
            q.setId(req.getQuestionnaireId());
            q.setSchoolYear(schoolYear);
            q.setStartDate(startDate);
            q.setEndedDate(endedDate);
            q.setGradeNo(req.getGradeNo());

            User user = UserSessionUtils.getUserFromSession();
            questionnaireRepository.updateToPublished(q, user.getUserName());
            //生成学生问卷
            userQuestionnaireRepository.createStudentQuestionAnswer(q.getId(), q.getGradeNo(), user.getUserName());
        } catch (HealthException e) {
            logger.error("发布问卷题目异常", e);
            resp = BaseResp.buildFailResp("保存问卷题目异常" + e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("发布问卷题目异常", e);
            resp = BaseResp.buildFailResp("发布问卷题目异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("发布问卷,resp:{}", resp);
        return resp;
    }

    /**
     * 删除问卷
     */
    @RequestMapping(value = "admin/review/deleteQuestionnaire", method = RequestMethod.POST)
    public BaseResp deleteQuestionnaire(@RequestBody QuestionnaireReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            logger.info("删除问卷,req,questionnaireId:{}", req.getQuestionnaireId());
            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);

            User user = UserSessionUtils.getUserFromSession();
            questionnaireRepository.updateToDeleted(req.getQuestionnaireId(), user.getUserName());
        } catch (HealthException e) {
            logger.error("删除问卷题目异常", e);
            resp = BaseResp.buildFailResp("删除问卷题目异常:" + e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("删除问卷题目异常", e);
            resp = BaseResp.buildFailResp("删除问卷题目异常", BaseResp.class);
        }
        logger.info("删除问卷,resp:{}", resp);
        return resp;
    }

    /**
     * 展示设置分数+纬度
     */
    @RequestMapping(value = "admin/review/showAddScoreAndLatitude", method = RequestMethod.POST)
    public BaseResp showAddScoreAndLatitude(@RequestBody QuestionnaireReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            logger.info("展示设置分数和纬度,req questionnaireId:{}", req.getQuestionnaireId());
            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            resp = questionnaireService.showAddScoreAndLatitude(req.getQuestionnaireId());
        } catch (Exception e) {
            logger.error("查询问卷题目分数纬度异常", e);
            resp = BaseResp.buildFailResp("查询问卷题目分数纬度异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("展示设置分数和纬度,resp:{}", resp);
        return resp;
    }

    /**
     * 设置分数+纬度
     * 
     * @return
     */
    @RequestMapping(value = "admin/review/addScoreAndLatitude", method = RequestMethod.POST)
    public BaseResp addScoreAndLatitude(@RequestBody AddQuestionnaireQuestionsInfo req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            logger.info("展示设置分数和纬度,req :{}", req);

            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            JavaAssert.isTrue(CollectionUtils.isNotEmpty(req.getQuestions()), ReturnCode.PARAM_ILLEGLE, "问卷问题不能为空",
                    HealthException.class);
            User user = UserSessionUtils.getUserFromSession();

            questionnaireService.addScoreAndLatitude(req.getQuestionnaireId(), req.getQuestions(), user.getUserName());
            resp.setData(req.getQuestionnaireId());
        } catch (Exception e) {
            logger.error("设置问卷题目分数纬度异常", e);
            resp = BaseResp.buildFailResp("设置问卷题目分数纬度异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("设置分数和纬度,resp:{}", resp);
        return resp;
    }

    /**
     * 展示添加问卷纬度描述设置
     * 
     * @return
     */
    @RequestMapping(value = "admin/review/showQuestionnaireLatitudeDesc", method = RequestMethod.POST)
    public BaseResp showQuestionnaireLatitudeDesc(@RequestBody QuestionnaireReq req) {

        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            logger.info("展示添加问卷纬度描述设置,req questionnaireId:{}", req.getQuestionnaireId());
            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);

            JSONObject json = questionnaireService.showQuestionnaireLatitudeScoreAndDesc(req.getQuestionnaireId());
            resp.setData(json);
        } catch (Exception e) {
            logger.error("展示添加问卷纬度描述异常", e);
            resp = BaseResp.buildFailResp("展示添加问卷纬度描述异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("展示添加问卷纬度描述设置,resp:{}", resp);
        return resp;
    }

    /**
     * 添加问卷纬度描述设置
     */
    @RequestMapping(value = "admin/review/addQuestionnaireLatitudeDesc", method = RequestMethod.POST)
    public BaseResp addQuestionnaireLatitudeDesc(@RequestBody AddQuesLatitudeDescReq req) {

        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);

            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            JavaAssert.isTrue(CollectionUtils.isNotEmpty(req.getLatitudes()), ReturnCode.PARAM_ILLEGLE, "纬度描述不能为空",
                    HealthException.class);
            User user = UserSessionUtils.getUserFromSession();

            questionnaireService.addQuestionnaireLatitudeDesc(req.getQuestionnaireId(), req.getLatitudes(),
                    user.getUserName());
            resp.setData(req.getQuestionnaireId());
        } catch (Exception e) {
            logger.error("展示添加问卷纬度描述异常", e);
            resp = BaseResp.buildFailResp("展示添加问卷纬度描述异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("添加问卷纬度描述设置,resp:{}", resp);
        return resp;
    }

    /**
     * 测评问卷:问卷对学生答题和老师测评统计
     */
    @RequestMapping(value = "admin/review/questionnaireAnswerSumary", method = RequestMethod.POST)
    public BaseResp questionnaireAnswerSumary(@RequestBody QuestionnaireReq req) {

        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            Page<Questionnaire> page = questionnaireService.questionnaireAnswerSumary(req.getGradeNo(), req.getTitle(),
                    req.getOffSet(), req.getLength(), QuestionnaireType.STUDENT.name());
            resp.setData(page);
        } catch (Exception e) {
            logger.error("展示测评问卷异常", e);
            resp = BaseResp.buildFailResp("展示测评问卷异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("展示测评问卷,resp:{}", resp);
        return resp;
    }

    /**
     * 测评年级班级学生明细
     * 
     * @param req
     * @return
     */
    @RequestMapping(value = "admin/review/questionnaireGradeClassSummary", method = RequestMethod.POST)
    public BaseResp questionnaireGradeClassSummary(@RequestBody QuestionnaireReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            logger.info("测评年级班级学生明细,req {}", req);

            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            JavaAssert.isTrue(req.getGradeNo() > -1, ReturnCode.PARAM_ILLEGLE, "年级不能为空", HealthException.class);
            JavaAssert.isTrue(req.getClassNo() > -1, ReturnCode.PARAM_ILLEGLE, "班级不能为空", HealthException.class);

            JSONArray ja = questionnaireService.questionnaireGradeClassSummary(req.getQuestionnaireId(),
                    req.getGradeNo(), req.getClassNo());
            resp.setData(ja);
        } catch (Exception e) {
            logger.error("展示测评问卷异常", e);
            resp = BaseResp.buildFailResp("展示测评问卷异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("测评年级班级学生明细,resp:{}", resp);
        return resp;
    }

    /**
     * 教师测评学生
     */
    @RequestMapping(value = "admin/review/showTeacherTestQuestionnaire", method = RequestMethod.POST)
    public BaseResp showTeacherTestQuestionnaire(@RequestBody QuestionnaireReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);

            logger.info("测评年级班级学生明细,req:{}", req);

            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            JavaAssert.isTrue(null != req.getStudentId(), ReturnCode.PARAM_ILLEGLE, "学生Id不能为空", HealthException.class);
            User user = UserSessionUtils.getUserFromSession();

            return questionnaireService.showTeacherTestQuestionnaire(req.getQuestionnaireId(), req.getStudentId());
        } catch (Exception e) {
            logger.error("展示测评问卷异常", e);
            resp = BaseResp.buildFailResp("展示测评问卷异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("展示教师测评学生,resp:{}", resp);
        return resp;
    }

    /**
     * 保存教师测评学生结果
     */
    @RequestMapping(value = "admin/review/addTeacherTestQuestionnaire", method = RequestMethod.POST)
    public BaseResp addTeacherTestQuestionnaire(HttpServletRequest req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            String idStr = req.getParameter("questionnaireId");

            String studentIdStr = req.getParameter("studentId");

            String questionOptionsJson = req.getParameter("questionOptions");
            logger.info("保存教师测评学生结果,req questionnaireId:{},studentId:{}", idStr, studentIdStr);

            JavaAssert.isTrue(StringUtils.isNotBlank(idStr), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            JavaAssert.isTrue(StringUtils.isNotBlank(studentIdStr), ReturnCode.PARAM_ILLEGLE, "学生Id不能为空",
                    HealthException.class);
            JavaAssert.isTrue(StringUtils.isNotBlank(questionOptionsJson), ReturnCode.PARAM_ILLEGLE, "教师对学生测评结果不能为空",
                    HealthException.class);

            User user = UserSessionUtils.getUserFromSession();

            questionnaireService.addTeacherTestQuestionnaire(Long.parseLong(idStr), Long.parseLong(studentIdStr),
                    questionOptionsJson, user.getUserName());
        } catch (Exception e) {
            logger.error("保存教师测评学生结果异常", e);
            resp = BaseResp.buildFailResp("保存教师测评学生结果异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("保存教师测评学生,resp:{}", resp);
        return resp;
    }

    //---报表
}
