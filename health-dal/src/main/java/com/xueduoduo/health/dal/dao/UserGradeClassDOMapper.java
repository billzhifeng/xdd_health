package com.xueduoduo.health.dal.dao;

import com.xueduoduo.health.dal.dataobject.UserGradeClassDO;
import com.xueduoduo.health.dal.dataobject.UserGradeClassDOExample;
import java.util.List;

public interface UserGradeClassDOMapper {
    long countByExample(UserGradeClassDOExample example);

    int insertSelective(UserGradeClassDO record);

    List<UserGradeClassDO> selectByExample(UserGradeClassDOExample example);

    UserGradeClassDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserGradeClassDO record);

    int updateByPrimaryKey(UserGradeClassDO record);
}