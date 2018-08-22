package com.xueduoduo.health.domain.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.java.common.base.Page;
import com.github.java.common.utils.JavaAssert;
import com.xueduoduo.health.dal.dao.UserDOMapper;
import com.xueduoduo.health.dal.dao.UserGradeClassDOMapper;
import com.xueduoduo.health.dal.dataobject.UserDO;
import com.xueduoduo.health.dal.dataobject.UserDOExample;
import com.xueduoduo.health.dal.dataobject.UserGradeClassDO;
import com.xueduoduo.health.dal.dataobject.UserGradeClassDOExample;
import com.xueduoduo.health.domain.common.HealthException;
import com.xueduoduo.health.domain.enums.IsDeleted;
import com.xueduoduo.health.domain.enums.ReturnCode;
import com.xueduoduo.health.domain.enums.UserRoleType;
import com.xueduoduo.health.domain.grade.GradeClass;

/**
 * @author wangzhifeng
 * @date 2018年8月15日 上午8:48:16
 */
@Service
public class UserRepository {

    @Autowired
    private UserDOMapper userDOMapper;

    @Transactional
    public void saveUser(User u) {
        UserDO ud = convertToUserDO(u);
        ud.setCreatedTime(new Date());
        ud.setUpdatedTime(new Date());
        userDOMapper.insertSelective(ud);
    }

    @Transactional
    public void updateStudent(User u) {
        UserDO ud = convertToUserDO(u);
        ud.setUpdatedTime(new Date());
        userDOMapper.updateByPrimaryKeySelective(ud);
    }

    @Transactional
    public void updateTheacherWithoutClass(User u) {
        UserDO ud = convertToUserDO(u);
        ud.setUpdatedTime(new Date());
        userDOMapper.updateByPrimaryKeySelective(ud);
    }

    @Transactional
    public void saveTheacherClass(User u, int gradeNo, int classNo) {

        UserGradeClassDO src = new UserGradeClassDO();
        if (gradeNo > 0) {
            src.setGradeNo(gradeNo);
        }
        if (classNo > 0) {
            src.setClassNo(classNo);
        }
        src.setUserId(u.getId());
        src.setIsDeleted(IsDeleted.N.name());
        src.setRole(u.getRole());
        src.setUpdatedTime(new Date());
        src.setCreatedTime(new Date());
        userGradeClassDOMapper.insertSelective(src);
    }

    @Transactional
    public void updateTheacherGradeClass(Long gradeClasssId, int gradeNo, int classNo) {

        UserGradeClassDO src = new UserGradeClassDO();
        src.setId(gradeClasssId);
        if (gradeNo > 0) {
            src.setGradeNo(gradeNo);
        }
        if (classNo > 0) {
            src.setClassNo(classNo);
        }
        src.setUpdatedTime(new Date());
        userGradeClassDOMapper.updateByPrimaryKeySelective(src);
    }

    @Transactional
    public void deleteTheacherClass(Long gradeClassId) {
        UserGradeClassDO src = new UserGradeClassDO();
        src.setId(gradeClassId);
        src.setIsDeleted(IsDeleted.Y.name());
        src.setUpdatedTime(new Date());
        userGradeClassDOMapper.updateByPrimaryKeySelective(src);
    }

    public User loadByLoginName(String loginName) {
        UserDOExample example = new UserDOExample();
        UserDOExample.Criteria cri = example.createCriteria();
        cri.andAccountNoEqualTo(loginName);
        cri.andIsDeletedEqualTo(IsDeleted.N.name());

        List<UserDO> users = userDOMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(users)) {
            UserDOExample example2 = new UserDOExample();
            UserDOExample.Criteria cri2 = example2.createCriteria();
            cri2.andIsDeletedEqualTo(IsDeleted.N.name());
            cri2.andPhoneEqualTo(loginName);
            users = userDOMapper.selectByExample(example2);
            if (CollectionUtils.isEmpty(users)) {
                return null;
            }
        }
        JavaAssert.isTrue(users.size() == 1, ReturnCode.DATA_NOT_EXIST, "登录名为：" + loginName + " 的用户不存在",
                HealthException.class);
        return convertToUserWithPasswd(users.get(0));
    }

    @Transactional
    public void deleteUser(Long userId) {
        UserDO u = userDOMapper.selectByPrimaryKey(userId);
        JavaAssert.isTrue(null != u, ReturnCode.DATA_NOT_EXIST, "用户不存在,用户id=" + userId, HealthException.class);

        u.setIsDeleted(IsDeleted.Y.name());
        u.setUpdatedTime(new Date());
        userDOMapper.updateByPrimaryKey(u);
    }

    @Autowired
    private UserGradeClassDOMapper userGradeClassDOMapper;

    /**
     * 获取班级
     * 
     * @param teacherId
     * @return
     */
    public List<GradeClass> loadGradeClassByTeacherId(Long teacherId) {
        UserGradeClassDOExample example = new UserGradeClassDOExample();
        UserGradeClassDOExample.Criteria cri = example.createCriteria();
        cri.andUserIdEqualTo(teacherId);
        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        List<UserGradeClassDO> list = userGradeClassDOMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(list)) {
            List<GradeClass> gcs = new ArrayList<GradeClass>();
            for (UserGradeClassDO gc : list) {
                GradeClass ugs = new GradeClass();
                ugs.setGradeClassId(gc.getId());
                ugs.setClassNo(gc.getClassNo());
                ugs.setGradeNo(gc.getGradeNo());
                gcs.add(ugs);
            }
            return gcs;
        }
        return null;
    }

    /**
     * 获取班级
     * 
     * @param teacherId
     * @return
     */
    public List<GradeClass> loadGradeClassByTeacherId(Long teacherId, int gradeNo, int classNo) {
        UserGradeClassDOExample example = new UserGradeClassDOExample();
        UserGradeClassDOExample.Criteria cri = example.createCriteria();
        cri.andUserIdEqualTo(teacherId);
        cri.andGradeNoEqualTo(gradeNo);
        cri.andClassNoEqualTo(classNo);
        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        List<UserGradeClassDO> list = userGradeClassDOMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(list)) {
            List<GradeClass> gcs = new ArrayList<GradeClass>();
            for (UserGradeClassDO gc : list) {
                GradeClass ugs = new GradeClass();
                ugs.setGradeClassId(gc.getId());
                ugs.setClassNo(gc.getClassNo());
                ugs.setGradeNo(gc.getGradeNo());
                gcs.add(ugs);
            }
            return gcs;
        }
        return null;
    }

    public User loadUserById(Long id) {

        UserDO u = userDOMapper.selectByPrimaryKey(id);
        JavaAssert.isTrue(null != u, ReturnCode.DATA_NOT_EXIST, "用户不存在,用户id=" + id, HealthException.class);
        return convertToUser(u);
    }

    public User loadUserWithPasswdById(Long id) {

        UserDO u = userDOMapper.selectByPrimaryKey(id);
        JavaAssert.isTrue(null != u, ReturnCode.DATA_NOT_EXIST, "用户不存在,用户id=" + id, HealthException.class);
        JavaAssert.isTrue(IsDeleted.N.name().equals(u.getIsDeleted()), ReturnCode.DATA_NOT_EXIST, "用户不存在,用户id=" + id,
                HealthException.class);
        User user = convertToUserWithPasswd(u);
        if (user.getRole().equals(UserRoleType.TEACHER.name())) {

            UserGradeClassDOExample example = new UserGradeClassDOExample();
            UserGradeClassDOExample.Criteria cri = example.createCriteria();
            cri.andUserIdEqualTo(id);
            cri.andIsDeletedEqualTo(IsDeleted.N.name());
            List<UserGradeClassDO> list = userGradeClassDOMapper.selectByExample(example);
            if (CollectionUtils.isNotEmpty(list)) {
                List<GradeClass> gcs = new ArrayList<GradeClass>();
                for (UserGradeClassDO gc : list) {
                    GradeClass ugs = new GradeClass();
                    ugs.setClassNo(gc.getClassNo());
                    ugs.setGradeNo(gc.getGradeNo());
                    gcs.add(ugs);
                }
                user.setGradeClassList(gcs);
            }
        }
        return user;
    }

    @Transactional
    public void changePasswd(Long id, String passwd) {
        UserDO u = new UserDO();
        u.setId(id);
        u.setPassword(passwd);
        u.setUpdatedTime(new Date());
        userDOMapper.updateByPrimaryKeySelective(u);
    }

    @Transactional
    public void saveTeacherWithGradeAndClass(User u, GradeClass gc) {
        saveUser(u);
        if (null == gc) {
            return;
        }
        UserGradeClassDO src = new UserGradeClassDO();
        src.setClassNo(gc.getClassNo());
        src.setGradeNo(gc.getGradeNo());
        src.setUserId(u.getId());
        src.setIsDeleted(IsDeleted.N.name());
        src.setRole(u.getRole());
        src.setUpdatedTime(new Date());
        src.setCreatedTime(new Date());
        userGradeClassDOMapper.insertSelective(src);

    }

    /**
     * 查询年级班级学生 +老师
     */
    public Page<User> loadUser(int gradeNo, int classNo, String userName, int offSet, int length, List<String> userRole,
                               Boolean withCount, String teacherOrStudent) {
        Page<User> p = new Page<User>();
        if ("TEACHER".equals(teacherOrStudent)) {
            //名字查询优先
            if (StringUtils.isNoneBlank(userName)) {
                UserDOExample example = new UserDOExample();
                UserDOExample.Criteria cri = example.createCriteria();
                userName = "%" + userName + "%";
                cri.andUserNameLike(userName);

                if (null != userRole && userRole.size() > 0) {
                    if (userRole.size() == 0) {
                        cri.andRoleEqualTo(userRole.get(0));
                    } else {
                        cri.andRoleIn(userRole);
                    }
                }

                if (offSet > -1 && length > 0) {
                    example.setOffSet(offSet);
                    example.setLength(length);

                    p.setOffSet(offSet);
                    p.setLength(length);
                }

                cri.andIsDeletedEqualTo(IsDeleted.N.name());
                if (withCount) {
                    Long totalCount = userDOMapper.countByExample(example);
                    p.setTotalCountNum(totalCount.intValue());
                }

                List<UserDO> users = userDOMapper.selectByExample(example);
                List<User> list = new ArrayList<User>();
                if (CollectionUtils.isNotEmpty(users)) {
                    for (UserDO src : users) {
                        list.add(convertToUser(src));
                    }
                }
                p.setPageData(list);
                return p;
            } else if (gradeNo > 0 || classNo > 0) {//班级年级查询
                UserGradeClassDOExample ue = new UserGradeClassDOExample();
                UserGradeClassDOExample.Criteria uecri = ue.createCriteria();
                if (gradeNo > 0) {
                    uecri.andGradeNoEqualTo(gradeNo);
                }
                if (classNo > 0) {
                    uecri.andClassNoEqualTo(classNo);
                }

                List<UserGradeClassDO> gs = userGradeClassDOMapper.selectByExample(ue);
                if (CollectionUtils.isEmpty(gs)) {
                    return p;
                }
                List<User> list = new ArrayList<User>();
                for (UserGradeClassDO c : gs) {
                    list.add(loadUserById(c.getUserId()));
                }
                p.setPageData(list);
                return p;
            } else {//查全部
                UserDOExample example = new UserDOExample();
                UserDOExample.Criteria cri = example.createCriteria();

                if (null != userRole && userRole.size() > 0) {
                    if (userRole.size() == 0) {
                        cri.andRoleEqualTo(userRole.get(0));
                    } else {
                        cri.andRoleIn(userRole);
                    }
                }

                if (offSet > -1 && length > 0) {
                    example.setOffSet(offSet);
                    example.setLength(length);

                    p.setOffSet(offSet);
                    p.setLength(length);
                }

                cri.andIsDeletedEqualTo(IsDeleted.N.name());
                if (withCount) {
                    Long totalCount = userDOMapper.countByExample(example);
                    p.setTotalCountNum(totalCount.intValue());
                }

                List<UserDO> users = userDOMapper.selectByExample(example);
                List<User> list = new ArrayList<User>();
                if (CollectionUtils.isNotEmpty(users)) {
                    for (UserDO src : users) {
                        list.add(convertToUser(src));
                    }
                }
                p.setPageData(list);
                return p;
            }
        } else {//学生
            UserDOExample example = new UserDOExample();
            UserDOExample.Criteria cri = example.createCriteria();
            userName = "%" + userName + "%";
            cri.andUserNameLike(userName);

            if (null != userRole && userRole.size() > 0) {
                if (userRole.size() == 0) {
                    cri.andRoleEqualTo(userRole.get(0));
                } else {
                    cri.andRoleIn(userRole);
                }
            }

            if (offSet > -1 && length > 0) {
                example.setOffSet(offSet);
                example.setLength(length);

                p.setOffSet(offSet);
                p.setLength(length);
            }

            if (gradeNo > 0) {
                cri.andGradeNoEqualTo(gradeNo);
            }
            if (classNo > 0) {
                cri.andClassNoEqualTo(classNo);
            }

            cri.andIsDeletedEqualTo(IsDeleted.N.name());
            if (withCount) {
                Long totalCount = userDOMapper.countByExample(example);
                p.setTotalCountNum(totalCount.intValue());
            }

            List<UserDO> users = userDOMapper.selectByExample(example);
            List<User> list = new ArrayList<User>();
            if (CollectionUtils.isNotEmpty(users)) {
                for (UserDO src : users) {
                    list.add(convertToUser(src));
                }
            }
            p.setPageData(list);
            return p;
        }
    }

    /**
     * 查询年级班级学生 +老师
     */
    public List<User> loadUser(int gradeNo, int classNo, String userName, int offSet, int length, String userRole,
                               String teacherOrStudent) {
        List<String> roles = new ArrayList<String>();
        roles.add(userRole);
        return loadUser(gradeNo, classNo, userName, offSet, length, roles, false, teacherOrStudent).getPageData();
    }

    /**
     * 查询年级班级学生 +老师
     */
    public List<User> loadUser(int gradeNo, int classNo, String teacherOrStudent) {
        return loadUser(gradeNo, classNo, null, -1, -1, null, teacherOrStudent);
    }

    private User convertToUser(UserDO src) {
        User tar = new User();
        BeanUtils.copyProperties(src, tar);
        tar.setPassword(null);
        tar.setUserStatus("正常");
        tar.setAddition(src.getGradeNoStr() + "年级" + src.getClassNo() + "班");
        tar.setUserStatus("正常");
        return tar;
    }

    private User convertToUserWithPasswd(UserDO src) {
        User tar = new User();
        BeanUtils.copyProperties(src, tar);
        tar.setUserStatus("正常");
        return tar;
    }

    private UserDO convertToUserDO(User src) {
        UserDO tar = new UserDO();
        BeanUtils.copyProperties(src, tar);
        return tar;
    }
}
