package com.xueduoduo.health.controller.dto;

import java.io.Serializable;

import com.alibaba.fastjson.JSONArray;
import com.github.java.common.base.Printable;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wangzhifeng
 * @date 2018年8月19日 下午4:58:32
 */
@Setter
@Getter
public class QuestionnaireReq extends Printable implements Serializable {
    private Long      id;
    private Long      questionnaireId;    //和ID 一样
    private String    schoolYear;
    private String    title;
    private String    questionnaireType;
    private int       length  = -1;
    private int       offSet  = -1;

    private int       gradeNo = -1;
    private int       classNo = -1;
    private String    startDate;
    private String    endedDate;

    private Long      studentId;
    private JSONArray questionOptionsJson;
}
