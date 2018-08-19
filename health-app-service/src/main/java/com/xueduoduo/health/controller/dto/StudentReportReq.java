package com.xueduoduo.health.controller.dto;

import java.io.Serializable;

import com.github.java.common.base.Printable;

import lombok.Getter;
import lombok.Setter;

/**
 * 学生档案
 * 
 * @author wangzhifeng
 * @date 2018年8月19日 下午4:58:32
 */
@Setter
@Getter
public class StudentReportReq extends Printable implements Serializable {
    private Long   studentId;
    private int    gradeNo = -1;
    private int    classNo = -1;
    private String userName;
    private int    length  = -1;
    private int    offSet  = -1;
}
