package com.xueduoduo.health.login;

import java.io.Serializable;

import com.github.java.common.base.Printable;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wangzhifeng
 * @date 2018年8月19日 下午4:55:52
 */
@Setter
@Getter
public class LoginReq extends Printable implements Serializable {

    private String loginName;
    private String passwd;

}
