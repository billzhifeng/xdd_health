package com.xueduoduo.health.domain.enums;

/**
 * 性别
 * 
 * @author wangzhifeng
 * @date 2018年8月17日 下午10:42:10
 */
public enum GenderType {
    MALE("男"), //男
    FEMALE("女"),;
    GenderType(String desc) {
        this.desc = desc;
    }

    private String desc;

    public String getDesc() {
        return desc;
    }

}
