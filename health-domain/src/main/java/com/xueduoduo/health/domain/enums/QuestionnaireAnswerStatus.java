package com.xueduoduo.health.domain.enums;

/**
 * 问卷答题状态
 * 
 * @author wangzhifeng
 * @date 2018年8月10日 下午11:06:36
 */
public enum QuestionnaireAnswerStatus {
    //INIT未开始\DOING回答中\DONE结束
    INIT("未答题"),
    DOING("测评中"),
    DONE("完成"),;
    private String desc;

    QuestionnaireAnswerStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

}
