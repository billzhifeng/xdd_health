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

import com.xueduoduo.health.dal.dao.UserDOMapper;
import com.xueduoduo.health.dal.dao.UserGradeClassDOMapper;
import com.xueduoduo.health.dal.dataobject.UserDO;
import com.xueduoduo.health.dal.dataobject.UserDOExample;
import com.xueduoduo.health.dal.dataobject.UserGradeClassDO;
import com.xueduoduo.health.domain.enums.IsDeleted;
import com.xueduoduo.health.domain.grade.GradeClass;

/**
 * @author wangzhifeng
 * @date 2018年8月15日 上午8:48:16
 */
@Service
public class UserRepository {

    @Autowired
    private UserDOMapper           userDOMapper;

    @Autowired
    private UserGradeClassDOMapper userGradeClassDOMapper;

    public User loadUserById(Long id) {

        UserDO u = userDOMapper.selectByPrimaryKey(id);
        if (null != u) {
            return convertToUser(u);
        }
        return null;
    }

    @Transactional
    public void saveUser(User u) {
        UserDO ud = convertToUserDO(u);
        userDOMapper.insertSelective(ud);
    }

    @Transactional
    public void saveTeacherWithGradeAndClass(User u, List<GradeClass> gcs) {
        saveUser(u);
        if (CollectionUtils.isEmpty(gcs)) {
            return;
        }
        for (GradeClass gc : gcs) {
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
    }

    /**
     * 查询年级班级学生 +老师
     */
    public List<User> loadUser(int gradeNo, int classNo, String userName, int offSet, int length, String userRole) {
        UserDOExample example = new UserDOExample();
        UserDOExample.Criteria cri = example.createCriteria();
        if (gradeNo > 0) {
            cri.andGradeNoEqualTo(gradeNo);
        }
        if (classNo > 0) {
            cri.andClassNoEqualTo(classNo);
        }
        if (StringUtils.isNoneBlank(userName)) {
            userName = "%" + userName + "%";
            cri.andUserNameLike(userName);
        }
        if (offSet > -1 && length > 0) {
            example.setOffSet(offSet);
            example.setLength(length);
        }

        if (StringUtils.isNotBlank(userRole)) {
            cri.andRoleEqualTo(userRole);
        }

        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        List<UserDO> users = userDOMapper.selectByExample(example);
        List<User> list = new ArrayList<User>();
        if (CollectionUtils.isNotEmpty(users)) {
            for (UserDO src : users) {
                list.add(convertToUser(src));
            }
        }
        return list;
    }

    /**
     * 查询年级班级学生 +老师
     */
    public List<User> loadUser(int gradeNo, int classNo) {
        return loadUser(gradeNo, classNo, null, -1, -1, null);
    }

    private User convertToUser(UserDO src) {
        User tar = new User();
        BeanUtils.copyProperties(src, tar);
        return tar;
    }

    private UserDO convertToUserDO(User src) {
        UserDO tar = new UserDO();
        BeanUtils.copyProperties(src, tar);
        return tar;
    }
}
