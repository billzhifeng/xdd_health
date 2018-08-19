package com.xueduoduo.health.domain.enums;

import com.xueduoduo.health.domain.common.HealthException;

/**
 * @author wangzhifeng
 * @date 2018年8月14日 下午2:19:41
 */
public enum QuestionnaireType {

    TEACHER("教师问卷"),
    STUDENT("学生问卷");

    private String desc;

    public static QuestionnaireType parse(String name) {
        for (QuestionnaireType t : QuestionnaireType.values()) {
            if (name.equals(t.name())) {
                return t;
            }
        }
        throw new HealthException(ReturnCode.DATA_NOT_EXIST, "name=" + name + "问卷类型不存在");
    }

    QuestionnaireType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
