package com.xueduoduo.health.controller.dto;

import java.io.Serializable;
import java.util.List;

import com.github.java.common.base.Printable;
import com.xueduoduo.health.domain.latitude.Latitude;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wangzhifeng
 * @date 2018年8月19日 下午6:12:17
 */
@Setter
@Getter
public class AddQuesLatitudeDescReq extends Printable implements Serializable {
    private Long           questionnaireId;
    private List<Latitude> latitudes;

}
