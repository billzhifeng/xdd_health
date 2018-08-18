package com.xueduoduo.health.domain.latitude;

import java.util.Date;
import java.util.List;

import com.xueduoduo.health.domain.questionnaire.QuestionnaireLatitudeScore;

/**
 * 纬度
 * 
 * @author wangzhifeng
 * @date 2018年8月12日 上午12:16:43
 */
public class Latitude {

    private String                           schoolYear;

    private Long                             id;

    private String                           displayName;

    private String                           addition;

    private Date                             createdTime;

    private String                           createdTimeStr;

    private Date                             updatedTime;

    private String                           updatedTimeStr;

    private String                           createor;

    private String                           isDeleted;
    //问卷下纬度分数和描述
    private List<QuestionnaireLatitudeScore> latitudeScoreDesc;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName == null ? null : displayName.trim();
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

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public List<QuestionnaireLatitudeScore> getLatitudeScoreDesc() {
        return latitudeScoreDesc;
    }

    public void setLatitudeScoreDesc(List<QuestionnaireLatitudeScore> latitudeScoreDesc) {
        this.latitudeScoreDesc = latitudeScoreDesc;
    }

    public String getCreatedTimeStr() {
        return createdTimeStr;
    }

    public void setCreatedTimeStr(String createdTimeStr) {
        this.createdTimeStr = createdTimeStr;
    }

    public String getUpdatedTimeStr() {
        return updatedTimeStr;
    }

    public void setUpdatedTimeStr(String updatedTimeStr) {
        this.updatedTimeStr = updatedTimeStr;
    }

}
