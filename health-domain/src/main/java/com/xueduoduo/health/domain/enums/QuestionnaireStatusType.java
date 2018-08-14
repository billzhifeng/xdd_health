package com.xueduoduo.health.domain.enums;

/**
 * 问卷状态
 * 
 * @author wangzhifeng
 * @date 2018年8月10日 下午11:06:36
 */
public enum QuestionnaireStatusType {
    //创建步骤:CREATED创建\PUBLISHED已发布
    CREATED("创建完成未发布"),
    PUBLISHED("已发布"),;
    private String desc;

    QuestionnaireStatusType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

}
