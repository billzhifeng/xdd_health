package com.xueduoduo.health.controller;

import java.util.List;

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
import com.xueduoduo.health.controller.dto.LatitudeReq;
import com.xueduoduo.health.controller.dto.StudentReportReq;
import com.xueduoduo.health.controller.dto.TeacherAcctReq;
import com.xueduoduo.health.domain.common.HealthException;
import com.xueduoduo.health.domain.enums.ReturnCode;
import com.xueduoduo.health.domain.enums.UserRoleType;
import com.xueduoduo.health.domain.user.User;
import com.xueduoduo.health.domain.user.UserRepository;

/**
 * @author billzhifeng 
 * @date 2018年8月20日 下午10:32:45
 */
@RestController
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(LatitudeController.class);

    @Autowired
    private UserRepository userRepository;
    /**
     * 学生账号列表
     */
    @RequestMapping(value = "account/student/list", method = RequestMethod.POST)
    public BaseResp studentList(@RequestBody StudentReportReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            Page<User> users = userRepository.loadUser(req.getGradeNo(), req.getClassNo(), req.getUserName(), req.getOffSet(), req.getLength(), UserRoleType.STUDENT.name(),true);
            resp.setData(users);
        } catch (HealthException e) {
            logger.error("查询学生账号列表异常", e);
            resp = BaseResp.buildFailResp("查询学生账号列表异常."+e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("查询学生账号列表异常", e);
            resp = BaseResp.buildFailResp("查询学生账号列表异常", BaseResp.class);
        }
        return resp;
    } 
    /**
     * 删除学生账号列表
     */
    @RequestMapping(value = "account/student/delete", method = RequestMethod.POST)
    public BaseResp studentDelete(@RequestBody StudentReportReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getStudentId(), ReturnCode.PARAM_ILLEGLE, "学生ID不能为空", HealthException.class);
            userRepository.deleteUser(req.getStudentId());
        } catch (HealthException e) {
            logger.error("查询学生账号列表异常", e);
            resp = BaseResp.buildFailResp("查询学生账号列表异常."+e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("查询学生账号列表异常", e);
            resp = BaseResp.buildFailResp("查询学生账号列表异常", BaseResp.class);
        }
        return resp;
    }
    /**
     * 删除教师账号列表
     */
    @RequestMapping(value = "account/teacher/delete", method = RequestMethod.POST)
    public BaseResp teacherDelete(@RequestBody TeacherAcctReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getTeacherId(), ReturnCode.PARAM_ILLEGLE, "教师ID不能为空", HealthException.class);
            userRepository.deleteUser(req.getTeacherId());
        } catch (HealthException e) {
            logger.error("查询教师账号列表异常", e);
            resp = BaseResp.buildFailResp("查询教师账号列表异常."+e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("查询教师账号列表异常", e);
            resp = BaseResp.buildFailResp("查询教师账号列表异常", BaseResp.class);
        }
        return resp;
    }
    /**
     * 添加教师账号列表
     */
    @RequestMapping(value = "account/teacher/add", method = RequestMethod.POST)
    public BaseResp teacherAdd(@RequestBody TeacherAcctReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getAccountNo(), ReturnCode.PARAM_ILLEGLE, "教师账号不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getUserName(), ReturnCode.PARAM_ILLEGLE, "教师姓名不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getPosition(), ReturnCode.PARAM_ILLEGLE, "教师职位不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getGender(), ReturnCode.PARAM_ILLEGLE, "教师性别不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getPhone(), ReturnCode.PARAM_ILLEGLE, "教师手机号不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getPasswd(), ReturnCode.PARAM_ILLEGLE, "教师密码不能为空", HealthException.class);
            
            User u = new User();
            u.setAccountNo(req.getAccountNo());
            u.setUserName(req.getUserName());
            u.setPosition(req.getPosition());
            u.setGender(req.getGender());
            u.setPhone(req.getPhone());
            u.setPassword(req.getPasswd());
//            u.setRole(UserRoleType.);
            userRepository.saveUser(u);
        } catch (HealthException e) {
            logger.error("查询教师账号列表异常", e);
            resp = BaseResp.buildFailResp("查询教师账号列表异常."+e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("查询教师账号列表异常", e);
            resp = BaseResp.buildFailResp("查询教师账号列表异常", BaseResp.class);
        }
        return resp;
    }
    
    /**
     * 教师账号列表
     */
    @RequestMapping(value = "account/student/list", method = RequestMethod.POST)
    public BaseResp teacherList(@RequestBody StudentReportReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            Page<User> users = userRepository.loadUser(req.getGradeNo(), req.getClassNo(), req.getUserName(), req.getOffSet(), req.getLength(), UserRoleType.STUDENT.name(),true);
            resp.setData(users);
        } catch (HealthException e) {
            logger.error("查询教师账号列表异常", e);
            resp = BaseResp.buildFailResp("查询教师账号列表异常."+e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("查询教师账号列表异常", e);
            resp = BaseResp.buildFailResp("查询教师账号列表异常", BaseResp.class);
        }
        return resp;
    }
}
