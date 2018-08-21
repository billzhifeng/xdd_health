package com.xueduoduo.health.controller.dto;

import java.io.Serializable;

import com.github.java.common.base.Printable;

import lombok.Getter;
import lombok.Setter;

/**
 * 教师账号
 * 
 * @author wangzhifeng
 * @date 2018年8月19日 下午4:58:32
 */
@Setter
@Getter
public class TeacherAcctReq extends Printable implements Serializable {
    private Long   teacherId;
    private int    gradeNo = -1;
    private int    classNo = -1;
    private String userName;
    private int    length  = -1;
    private int    offSet  = -1;
    
    private String accountNo;
    private String position;
    private String phone;
    private String gender;
    private String passwd;
}
