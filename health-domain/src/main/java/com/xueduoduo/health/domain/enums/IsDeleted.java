package com.xueduoduo.health.domain.enums;

/**
 * @author wangzhifeng
 * @date 2018年8月11日 下午8:39:16
 */
public enum IsDeleted {
    Y("被删除"),
    N("有效"),;
    IsDeleted(String desc) {
        this.desc = desc;
    }

    private String desc;

    public String getDesc() {
        return desc;
    }

}
