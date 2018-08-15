package com.xueduoduo.health.domain.enums;

/**
 * 角色类型
 * 
 * @author wangzhifeng
 * @date 2018年8月10日 下午11:06:36
 */
public enum UserRoleType {

    STUDENT("学生"),
    TEACHER("教师"),
    CLASS_HEADER("班主任"),
    MASTER("管理员"),;
    private String desc;

    UserRoleType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

}
