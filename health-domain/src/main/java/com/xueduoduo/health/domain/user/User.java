package com.xueduoduo.health.domain.user;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wangzhifeng
 * @date 2018年8月10日 下午10:42:29
 */
@Setter
@Getter
public class User {
    private Long    id;

    private String  role;

    private String  userName;

    private String  position;

    private String  gender;

    private String  accountNo;

    private String  studentNo;

    private Integer gradeNo;

    private Integer classNo;

    private String  password;

    private String  phone;

    private String  userStatus;

    private String  addition;

    private String  createorId;

    private Date    createdTime;

    private Date    updatedTime;

    private String  isDeleted;

    private String  headerImg;
}
