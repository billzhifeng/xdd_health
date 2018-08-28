package com.xueduoduo.health.domain.enums;

import com.github.java.common.base.BaseEnum;

/**
 * @author wangzhifeng
 * @date 2018年8月10日 下午10:38:03
 */
public enum ReturnCode implements BaseEnum {
    DATA_NOT_EXIST(100, "数据不存在"),
    OPERATOR_DATE_ILLEGLE(101, "业务动作时间不正确"),
    DB_ERROR(102, "DB异常"),
    PARAM_ILLEGLE(103, "参数异常"),
    NOT_LOGIN(104, "未登录"),
    NO_AUTH(403, "无权限");
    ReturnCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private int    code;
    private String desc;

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getDesc() {
        return null;
    }

}
