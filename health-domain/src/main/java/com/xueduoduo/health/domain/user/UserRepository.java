package com.xueduoduo.health.domain.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xueduoduo.health.dal.dao.UserDOMapper;
import com.xueduoduo.health.dal.dataobject.UserDO;
import com.xueduoduo.health.dal.dataobject.UserDOExample;
import com.xueduoduo.health.domain.enums.IsDeleted;

/**
 * @author wangzhifeng
 * @date 2018年8月15日 上午8:48:16
 */
@Service
public class UserRepository {

    @Autowired
    private UserDOMapper userDOMapper;

    public User loadUserById(Long id) {

        UserDO u = userDOMapper.selectByPrimaryKey(id);
        if (null != u) {
            return convertToUser(u);
        }
        return null;
    }

    /**
     * 查询年级班级学生
     */
    public List<User> loadUser(int gradeNo, int classNo) {
        UserDOExample example = new UserDOExample();
        UserDOExample.Criteria cri = example.createCriteria();
        if (gradeNo > 0) {
            cri.andGradeNoEqualTo(gradeNo);
        }
        if (classNo > 0) {
            cri.andClassNoEqualTo(classNo);
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

    private User convertToUser(UserDO src) {
        User tar = new User();
        BeanUtils.copyProperties(src, tar);
        return tar;
    }
}