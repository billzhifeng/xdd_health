package com.xueduoduo.health.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
import com.xueduoduo.health.controller.dto.AcctReq;
import com.xueduoduo.health.controller.dto.StudentReportReq;
import com.xueduoduo.health.domain.common.HealthException;
import com.xueduoduo.health.domain.enums.ReturnCode;
import com.xueduoduo.health.domain.enums.UserRoleType;
import com.xueduoduo.health.domain.grade.GradeClass;
import com.xueduoduo.health.domain.user.User;
import com.xueduoduo.health.domain.user.UserRepository;

/**
 * 账户管理
 * 
 * @author billzhifeng
 * @date 2018年8月20日 下午10:32:45
 */
@RestController
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(LatitudeController.class);

    @Autowired
    private UserRepository      userRepository;

    /**
     * 报表中：年级班级下学生列表
     */
    @RequestMapping(value = "admin/account/student/gradeClassStudents", method = RequestMethod.POST)
    public BaseResp gradeClassStudents(@RequestBody StudentReportReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            List<User> users = userRepository.loadUser(req.getGradeNo(), req.getClassNo(), req.getUserName(),
                    req.getOffSet(), req.getLength(), UserRoleType.STUDENT.name(), "STUDENT");
            Collections.sort(users, Comparator.comparing(User::getStudentNo));
            resp.setData(users);
        } catch (HealthException e) {
            logger.error("查询学生账号列表异常", e);
            resp = BaseResp.buildFailResp("查询学生账号列表异常." + e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("查询学生账号列表异常", e);
            resp = BaseResp.buildFailResp("查询学生账号列表异常", BaseResp.class);
        }
        return resp;
    }

    /**
     * 学生账号列表
     */
    @RequestMapping(value = "admin/account/student/list", method = RequestMethod.POST)
    public BaseResp studentList(@RequestBody StudentReportReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            List<String> roles = new ArrayList<String>();
            roles.add(UserRoleType.STUDENT.name());
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            Page<User> users = userRepository.loadUser(req.getGradeNo(), req.getClassNo(), req.getUserName(),
                    req.getOffSet(), req.getLength(), roles, true, "STUDENT");
            resp.setData(users);
        } catch (HealthException e) {
            logger.error("查询学生账号列表异常", e);
            resp = BaseResp.buildFailResp("查询学生账号列表异常." + e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("查询学生账号列表异常", e);
            resp = BaseResp.buildFailResp("查询学生账号列表异常", BaseResp.class);
        }
        return resp;
    }

    /**
     * 添加学生账号
     */
    @RequestMapping(value = "admin/account/student/add", method = RequestMethod.POST)
    public BaseResp studentAdd(@RequestBody AcctReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getAccountNo(), ReturnCode.PARAM_ILLEGLE, "学生账号不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getStudentNo(), ReturnCode.PARAM_ILLEGLE, "学籍号不能为空", HealthException.class);
            JavaAssert.isTrue(req.getGradeNo() > 0, ReturnCode.PARAM_ILLEGLE, "学生年级不能为空", HealthException.class);
            JavaAssert.isTrue(req.getClassNo() > 0, ReturnCode.PARAM_ILLEGLE, "学生班级不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getUserName(), ReturnCode.PARAM_ILLEGLE, "学生姓名不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getGender(), ReturnCode.PARAM_ILLEGLE, "学生性别不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getPasswd(), ReturnCode.PARAM_ILLEGLE, "学生密码不能为空", HealthException.class);

            User u = new User();
            u.setAccountNo(req.getAccountNo());
            u.setStudentNo(req.getStudentNo());
            u.setGradeNo(req.getGradeNo());
            u.setClassNo(req.getClassNo());
            u.setUserName(req.getUserName());
            u.setGender(req.getGender());
            u.setPhone(req.getPhone());
            u.setPassword(req.getPasswd());
            u.setRole(UserRoleType.STUDENT.name());
            userRepository.saveUser(u);
        } catch (HealthException e) {
            logger.error("添加学生账号异常", e);
            resp = BaseResp.buildFailResp("添加学生账号异常." + e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("添加学生账号异常", e);
            resp = BaseResp.buildFailResp("添加学生账号异常", BaseResp.class);
        }
        return resp;
    }

    /**
     * 编辑学生账号
     */
    @RequestMapping(value = "admin/account/student/edit", method = RequestMethod.POST)
    public BaseResp studentEdit(@RequestBody AcctReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getAccountNo(), ReturnCode.PARAM_ILLEGLE, "学生账号不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getStudentNo(), ReturnCode.PARAM_ILLEGLE, "学籍号不能为空", HealthException.class);
            JavaAssert.isTrue(req.getGradeNo() > 0, ReturnCode.PARAM_ILLEGLE, "学生年级不能为空", HealthException.class);
            JavaAssert.isTrue(req.getClassNo() > 0, ReturnCode.PARAM_ILLEGLE, "学生班级不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getUserName(), ReturnCode.PARAM_ILLEGLE, "学生姓名不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getGender(), ReturnCode.PARAM_ILLEGLE, "学生性别不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getPasswd(), ReturnCode.PARAM_ILLEGLE, "学生密码不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getStudentId(), ReturnCode.PARAM_ILLEGLE, "学生ID不能为空", HealthException.class);

            User u = new User();
            u.setId(req.getStudentId());
            u.setAccountNo(req.getAccountNo());
            u.setStudentNo(req.getStudentNo());
            u.setGradeNo(req.getGradeNo());
            u.setClassNo(req.getClassNo());
            u.setUserName(req.getUserName());
            u.setGender(req.getGender());
            u.setPassword(req.getPasswd());
            userRepository.updateStudent(u);
        } catch (HealthException e) {
            logger.error("编辑学生账号异常", e);
            resp = BaseResp.buildFailResp("编辑学生账号异常." + e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("编辑学生账号异常", e);
            resp = BaseResp.buildFailResp("编辑学生账号异常", BaseResp.class);
        }
        return resp;
    }

    /**
     * 删除学生账号
     */
    @RequestMapping(value = "admin/account/student/delete", method = RequestMethod.POST)
    public BaseResp studentDelete(@RequestBody StudentReportReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getStudentId(), ReturnCode.PARAM_ILLEGLE, "学生ID不能为空", HealthException.class);
            userRepository.deleteUser(req.getStudentId());
        } catch (HealthException e) {
            logger.error("删除学生账号异常", e);
            resp = BaseResp.buildFailResp("删除学生账号异常." + e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("删除学生账号异常", e);
            resp = BaseResp.buildFailResp("删除学生账号异常", BaseResp.class);
        }
        return resp;
    }

    //------
    /**
     * 删除教师账号列表
     */
    @RequestMapping(value = "admin/account/teacher/delete", method = RequestMethod.POST)
    public BaseResp teacherDelete(@RequestBody AcctReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getTeacherId(), ReturnCode.PARAM_ILLEGLE, "教师ID不能为空", HealthException.class);
            User u = userRepository.loadUserById(req.getTeacherId());
            JavaAssert.isTrue(null != u, ReturnCode.PARAM_ILLEGLE, "教师不存在,ID=" + req.getTeacherId(),
                    HealthException.class);

            userRepository.deleteUser(req.getTeacherId());
        } catch (HealthException e) {
            logger.error("删除教师账号异常", e);
            resp = BaseResp.buildFailResp("删除教师账号异常." + e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("删除教师账号异常", e);
            resp = BaseResp.buildFailResp("删除教师账号异常", BaseResp.class);
        }
        return resp;
    }

    /**
     * 添加教师账号
     */
    @RequestMapping(value = "admin/account/teacher/add", method = RequestMethod.POST)
    public BaseResp teacherAdd(@RequestBody AcctReq req) {
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

            if (UserRoleType.CLASS_HEADER.name().equals(req.getPosition())) {
                u.setRole(UserRoleType.CLASS_HEADER.name());
            } else {
                u.setRole(UserRoleType.TEACHER.name());
            }
            userRepository.saveUser(u);
        } catch (HealthException e) {
            logger.error("添加教师账号异常", e);
            resp = BaseResp.buildFailResp("添加教师账号异常." + e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("添加教师账号异常", e);
            resp = BaseResp.buildFailResp("添加教师账号异常", BaseResp.class);
        }
        return resp;
    }

    /**
     * 编辑教师账号
     */
    @RequestMapping(value = "admin/account/teacher/edit", method = RequestMethod.POST)
    public BaseResp adminTeacherEdit(@RequestBody AcctReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getTeacherId(), ReturnCode.PARAM_ILLEGLE, "教师ID不能为空", HealthException.class);
            User user = userRepository.loadUserById(req.getTeacherId());
            JavaAssert.isTrue(null != user, ReturnCode.PARAM_ILLEGLE, "教师不存在,ID=" + req.getTeacherId(),
                    HealthException.class);

            User u = new User();
            u.setAccountNo(req.getAccountNo());
            u.setUserName(req.getUserName());
            u.setPosition(req.getPosition());
            u.setGender(req.getGender());
            u.setPhone(req.getPhone());
            u.setPassword(req.getPasswd());
            u.setId(req.getTeacherId());

            if (UserRoleType.CLASS_HEADER.name().equals(req.getPosition())) {
                u.setRole(UserRoleType.CLASS_HEADER.name());
            } else {
                u.setRole(UserRoleType.TEACHER.name());
            }
            userRepository.updateTheacherWithoutClass(u);
        } catch (HealthException e) {
            logger.error("添加教师账号异常", e);
            resp = BaseResp.buildFailResp("添加教师账号异常." + e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("添加教师账号异常", e);
            resp = BaseResp.buildFailResp("添加教师账号异常", BaseResp.class);
        }
        return resp;
    }

    /**
     * 教师账号列表
     */
    @RequestMapping(value = "admin/account/teacher/list", method = RequestMethod.POST)
    public BaseResp teacherList(@RequestBody StudentReportReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            List<String> roles = new ArrayList<String>();
            roles.add(UserRoleType.TEACHER.name());
            roles.add(UserRoleType.CLASS_HEADER.name());
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            Page<User> users = userRepository.loadUser(req.getGradeNo(), req.getClassNo(), req.getUserName(),
                    req.getOffSet(), req.getLength(), roles, true, "TEACHER");
            resp.setData(users);
        } catch (HealthException e) {
            logger.error("查询教师账号列表异常", e);
            resp = BaseResp.buildFailResp("查询教师账号列表异常." + e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("查询教师账号列表异常", e);
            resp = BaseResp.buildFailResp("查询教师账号列表异常", BaseResp.class);
        }
        return resp;
    }

    //---教师个人中心------------------------------
    /**
     * 教师个人中心展示
     */
    @RequestMapping(value = "admin/account/teacherCenter/view", method = RequestMethod.POST)
    public BaseResp teacherCenter(@RequestBody AcctReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getTeacherId(), ReturnCode.PARAM_ILLEGLE, "教师ID不能为空", HealthException.class);

            User u = userRepository.loadUserWithPasswdById(req.getTeacherId());
            resp.setData(u);
        } catch (HealthException e) {
            logger.error("教师个人中心展示异常", e);
            resp = BaseResp.buildFailResp("教师个人中心展示异常." + e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("教师个人中心展示异常", e);
            resp = BaseResp.buildFailResp("教师个人中心展示异常", BaseResp.class);
        }
        return resp;
    }

    /**
     * 教师个人中心编辑:姓名、性别、手机号、密码、头像(不含年级班级)
     */
    @RequestMapping(value = "admin/account/teacherCenter/edit", method = RequestMethod.POST)
    public BaseResp teacherEdit(@RequestBody AcctReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getTeacherId(), ReturnCode.PARAM_ILLEGLE, "教师ID不能为空", HealthException.class);

            User t = userRepository.loadUserWithPasswdById(req.getTeacherId());
            JavaAssert.isTrue(null != t, ReturnCode.PARAM_ILLEGLE, "ID=" + req.getTeacherId() + " 的教师不存在",
                    HealthException.class);
            User u = new User();
            u.setId(req.getTeacherId());
            u.setUserName(req.getUserName());
            u.setPhone(req.getPhone());
            u.setGender(req.getGender());
            u.setHeaderImg(req.getHeadImgUrl());

            if (StringUtils.isNoneBlank(req.getPasswd())) {
                JavaAssert.isTrue(StringUtils.isNoneBlank(req.getOriPasswd()), ReturnCode.PARAM_ILLEGLE, "原始密码不能为空",
                        HealthException.class);

                JavaAssert.isTrue(t.getPassword().equals(req.getOriPasswd()), ReturnCode.PARAM_ILLEGLE, "原始密码不正确",
                        HealthException.class);
                u.setPassword(req.getPasswd());
            }
            userRepository.updateTheacherWithoutClass(u);

        } catch (HealthException e) {
            logger.error("编辑教师个人中心异常", e);
            resp = BaseResp.buildFailResp("编辑教师个人中心异常." + e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("编辑教师个人中心异常", e);
            resp = BaseResp.buildFailResp("编辑教师个人中心异常", BaseResp.class);
        }
        return resp;
    }

    /**
     * 教师个人中心修改班级
     */
    @RequestMapping(value = "admin/account/teacherCenter/editClass", method = RequestMethod.POST)
    public BaseResp teacherEditClass(@RequestBody AcctReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getTeacherId(), ReturnCode.PARAM_ILLEGLE, "教师ID不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getGradeClassId(), ReturnCode.PARAM_ILLEGLE, "班级ID不能为空",
                    HealthException.class);
            JavaAssert.isTrue(0 < req.getClassNo(), ReturnCode.PARAM_ILLEGLE, "班级必须大于0", HealthException.class);
            JavaAssert.isTrue(0 < req.getGradeNo(), ReturnCode.PARAM_ILLEGLE, "年级必须大于0", HealthException.class);

            User t = userRepository.loadUserById(req.getTeacherId());
            JavaAssert.isTrue(null != t, ReturnCode.PARAM_ILLEGLE, "ID=" + req.getTeacherId() + " 的教师不存在",
                    HealthException.class);

            userRepository.updateTheacherGradeClass(req.getGradeClassId(), req.getGradeNo(), req.getClassNo());

        } catch (HealthException e) {
            logger.error("教师个人中心修改班级异常", e);
            resp = BaseResp.buildFailResp("教师个人中心修改班级异常." + e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("教师个人中心修改班级异常", e);
            resp = BaseResp.buildFailResp("教师个人中心修改班级异常", BaseResp.class);
        }
        return resp;
    }

    /**
     * 教师个人中心添加班级
     */
    @RequestMapping(value = "admin/account/teacherCenter/addClass", method = RequestMethod.POST)
    public BaseResp teacherAddClass(@RequestBody AcctReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getTeacherId(), ReturnCode.PARAM_ILLEGLE, "教师ID不能为空", HealthException.class);
            JavaAssert.isTrue(0 < req.getClassNo(), ReturnCode.PARAM_ILLEGLE, "班级必须大于0", HealthException.class);
            JavaAssert.isTrue(0 < req.getGradeNo(), ReturnCode.PARAM_ILLEGLE, "年级必须大于0", HealthException.class);

            User t = userRepository.loadUserById(req.getTeacherId());
            JavaAssert.isTrue(null != t, ReturnCode.PARAM_ILLEGLE, "ID=" + req.getTeacherId() + " 的教师不存在",
                    HealthException.class);
            List<GradeClass> ugs = userRepository.loadGradeClassByTeacherId(t.getId(), req.getGradeNo(),
                    req.getClassNo());
            JavaAssert.isTrue(CollectionUtils.isEmpty(ugs), ReturnCode.PARAM_ILLEGLE, "该教师已存在该年级和班级",
                    HealthException.class);
            userRepository.saveTheacherClass(t, req.getGradeNo(), req.getClassNo());

        } catch (HealthException e) {
            logger.error("教师个人中心添加班级异常", e);
            resp = BaseResp.buildFailResp("教师个人中心添加班级异常." + e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("教师个人中心添加班级异常", e);
            resp = BaseResp.buildFailResp("教师个人中心添加班级异常", BaseResp.class);
        }
        return resp;
    }

    /**
     * 教师个人中心删除班级
     */
    @RequestMapping(value = "admin/account/teacherCenter/deleteClass", method = RequestMethod.POST)
    public BaseResp teacherDeleteClass(@RequestBody AcctReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getTeacherId(), ReturnCode.PARAM_ILLEGLE, "教师ID不能为空", HealthException.class);

            User t = userRepository.loadUserById(req.getTeacherId());
            JavaAssert.isTrue(null != t, ReturnCode.PARAM_ILLEGLE, "ID=" + req.getTeacherId() + " 的教师不存在",
                    HealthException.class);
            if (UserRoleType.CLASS_HEADER.name().equals(t.getPosition())) {
                resp.setReturnCode(ReturnCode.PARAM_ILLEGLE.getCode());
                resp.setReturnMsg("班主任不得删除班级");
                return resp;
            } else {
                userRepository.deleteTheacherClass(req.getGradeClassId());
            }

        } catch (HealthException e) {
            logger.error("编辑教师个人中心异常", e);
            resp = BaseResp.buildFailResp("编辑教师个人中心异常." + e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("编辑教师个人中心异常", e);
            resp = BaseResp.buildFailResp("编辑教师个人中心异常", BaseResp.class);
        }
        return resp;
    }
}
