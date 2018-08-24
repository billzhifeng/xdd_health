package com.xueduoduo.health.domain.questionnaire;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 问卷纬度分值及说明
 * 
 * @author wangzhifeng
 * @date 2018年8月11日 下午8:43:17
 */
public class QuestionnaireLatitudeScore {
    private Long       id;

    private Long       questionnaireId;

    private Long       latitudeId;

    private BigDecimal scoreMin;

    private BigDecimal scoreMax;

    private String     comment;

    private String     addition;

    private Date       createdTime;

    private Date       updatedTime;

    private String     createor;

    private String     isDeleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(Long questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public Long getLatitudeId() {
        return latitudeId;
    }

    public void setLatitudeId(Long latitudeId) {
        this.latitudeId = latitudeId;
    }

    public BigDecimal getScoreMin() {
        return scoreMin;
    }

    public void setScoreMin(BigDecimal scoreMin) {
        this.scoreMin = scoreMin;
    }

    public BigDecimal getScoreMax() {
        return scoreMax;
    }

    public void setScoreMax(BigDecimal scoreMax) {
        this.scoreMax = scoreMax;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
    }

    public String getAddition() {
        return addition;
    }

    public void setAddition(String addition) {
        this.addition = addition == null ? null : addition.trim();
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getCreateor() {
        return createor;
    }

    public void setCreateor(String createor) {
        this.createor = createor == null ? null : createor.trim();
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted == null ? null : isDeleted.trim();
    }
}
