package com.xueduoduo.health.controller.dto;

import java.io.Serializable;

import com.github.java.common.base.Printable;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wangzhifeng
 * @date 2018年8月19日 下午4:58:32
 */
@Setter
@Getter
public class LatitudeReq extends Printable implements Serializable {
    private Long   id;
    private String schoolYear;
    private String displayName;
    private int    length = -1;
    private int    offSet = -1;
}
