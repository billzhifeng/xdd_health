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
import com.xueduoduo.health.dal.dataobject.UserDO;
import com.xueduoduo.health.dal.dataobject.UserDOExample;
import com.xueduoduo.health.domain.common.HealthException;
import com.xueduoduo.health.domain.enums.IsDeleted;
import com.xueduoduo.health.domain.enums.ReturnCode;

/**
 * @author wangzhifeng
 * @date 2018年8月15日 上午8:48:16
 */
@Service
public class UserRepository {

    @Autowired
    private UserDOMapper userDOMapper;
    
    @Transactional
    public void saveUser(User u){
       UserDO ud = convertToUserDO(u);
       ud.setCreatedTime(new Date());
       ud.setUpdatedTime(new Date());
       ud.setRole(u.getRole());
       userDOMapper.insertSelective(ud);
    }
    
    @Transactional
    public void deleteUser(Long userId){
        UserDO u = userDOMapper.selectByPrimaryKey(userId);
        JavaAssert.isTrue(null!=u, ReturnCode.DATA_NOT_EXIST,"用户不存在,用户id="+userId,HealthException.class);
        
        u.setIsDeleted(IsDeleted.Y.name());
        userDOMapper.updateByPrimaryKey(u);
    }

    public User loadUserById(Long id) {

        UserDO u = userDOMapper.selectByPrimaryKey(id);
        JavaAssert.isTrue(null!=u, ReturnCode.DATA_NOT_EXIST,"学生不存在,学生id="+id,HealthException.class);
        return convertToUser(u);
    }
    
    public User loadUserWithPasswdById(Long id) {

        UserDO u = userDOMapper.selectByPrimaryKey(id);
        JavaAssert.isTrue(null!=u, ReturnCode.DATA_NOT_EXIST,"学生不存在,学生id="+id,HealthException.class);
        return convertToUserWithPasswd(u);
    }
    
    /**
     * 查询年级班级学生 +老师
     */
    public Page<User> loadUser(int gradeNo, int classNo, String userName, int offSet,
                               int length, String userRole,Boolean withCount) {
        Page<User> p = new Page<User>();
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
            
            p.setOffSet(offSet);
            p.setLength(length);
        }

        if (StringUtils.isNotBlank(userRole)) {
            cri.andRoleEqualTo(userRole);
        }

        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        if(withCount){
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

    /**
     * 查询年级班级学生 +老师
     */
    public List<User> loadUser(int gradeNo, int classNo, String userName, int offSet, int length, String userRole) {
        return loadUser(gradeNo,classNo,userName,offSet,length,userRole,false).getPageData();
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
        tar.setPassword(null);
        tar.setUserStatus("正常");
        tar.setAddition(src.getGradeNoStr()+"年级"+src.getClassNo()+"班");
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
