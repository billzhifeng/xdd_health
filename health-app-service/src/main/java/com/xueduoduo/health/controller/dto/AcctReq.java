package com.xueduoduo.health.controller.dto;

import java.io.Serializable;

import com.alibaba.fastjson.JSONArray;
import com.github.java.common.base.Printable;

import lombok.Getter;
import lombok.Setter;

/**
 * 账号
 * 
 * @author wangzhifeng
 * @date 2018年8月19日 下午4:58:32
 */
@Setter
@Getter
public class AcctReq extends Printable implements Serializable {
    private Long      teacherId;
    private Long      studentId;
    private int       gradeNo = -1;
    private int       classNo = -1;
    private String    userName;
    private int       length  = -1;
    private int       offSet  = -1;

    //年级班级ID
    private Long      gradeClassId;

    //学籍号
    private String    studentNo;

    private String    accountNo;
    private String    position;
    private String    phone;
    private String    gender;
    private String    passwd;

    //    原始密码
    private String    oriPasswd;

    //头像
    private String    headImgUrl;

    private JSONArray gradeClasses;

}
