package com.xueduoduo.health.domain.common;

import com.github.java.common.base.BaseEnum;
import com.github.java.common.base.BaseException;

/**
 * @author wangzhifeng
 * @date 2018年8月11日 上午11:59:49
 */
public class HealthException extends BaseException {
    private static final long serialVersionUID = 1036359233490623555L;

    public HealthException() {
    }

    public HealthException(BaseEnum code, String msg) {
        super.setReturnCode(code.getCode());
        super.setReturnMsg(msg);
    }

    @Override
    public String getMessage() {
        return super.getReturnMsg();
    }
}
