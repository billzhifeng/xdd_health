package com.xueduoduo.health.controller.dto;

import java.io.Serializable;
import java.util.List;

import com.github.java.common.base.Printable;
import com.xueduoduo.health.domain.questionnaire.Question;

/**
 * @author wangzhifeng
 * @date 2018年8月19日 下午4:28:37
 */
public class AddQuestionnaireQuestionsInfo extends Printable implements Serializable {

    private Long           questionnaireId;
    private String         title;
    private String         introduction;

    private List<Question> questions;

    public Long getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(Long questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

}
