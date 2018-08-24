package com.xueduoduo.health.controller.dto;

import java.io.Serializable;

import com.github.java.common.base.Printable;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wangzhifeng
 * @date 2018年8月19日 下午6:41:20
 */
@Setter
@Getter
public class StudentQuestionnairReq extends Printable implements Serializable {

    private Long   questionnaireId;//             = req.getParameter("questionnaireId");

    private Long   studentId;      //       = req.getParameter("studentId");

    private Long   questionId;     //     = req.getParameter("questionId");

    private Long   optionId;       //       = req.getParameter("optionId");

    private Long   answerId;       //       = req.getParameter("answerId");

    private Long   nextQuestionId; //  = req.getParameter("nextQuestionId");
    private Long   preQuestionId;  //   = req.getParameter("preQuestionId");

    private String passwd;

    private String headImgUrl;     //头像图片地址
}
