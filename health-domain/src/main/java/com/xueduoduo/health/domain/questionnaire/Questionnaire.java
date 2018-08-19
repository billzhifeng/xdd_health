package com.xueduoduo.health.domain.questionnaire;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 问卷
 * 
 * @author wangzhifeng
 * @date 2018年8月11日 下午8:43:17
 */
public class Questionnaire {
    private Long       id;

    private String     questionnaireType;

    private String     title;

    private String     introduction;

    private String     createStatus;

    private String     isDeleted;

    private Integer    gradeNo;

    private Date       startDate;

    private Date       endedDate;

    private String     schoolYear;
    //题目数量
    private Integer    count;

    private String     addition;

    private Date       createdTime;

    private String     createdTimeStr;

    private Date       updatedTime;
    private String     updatedTimeStr;

    private String     createor;

    //已经测评学生人数统计
    private int        studentAnswerCount;
    //总学生人数
    private int        totalStudentCount;
    //学生未测评人数
    private int        studentNotAnswerCount;
    //完成测评比例
    private BigDecimal answeredRate;

    public int getStudentnotAnswerCount() {
        studentNotAnswerCount = totalStudentCount - studentAnswerCount;
        return studentNotAnswerCount;
    }

    public int getStudentAnswerCount() {
        return studentAnswerCount;
    }

    public void setStudentAnswerCount(int studentAnswerCount) {
        this.studentAnswerCount = studentAnswerCount;
    }

    public int getTotalStudentCount() {
        return totalStudentCount;
    }

    public void setTotalStudentCount(int totalStudentCount) {
        this.totalStudentCount = totalStudentCount;
    }

    public BigDecimal getAnsweredRate() {
        return answeredRate;
    }

    public void setAnsweredRate(BigDecimal answeredRate) {
        this.answeredRate = answeredRate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionnaireType() {
        return questionnaireType;
    }

    public void setQuestionnaireType(String questionnaireType) {
        this.questionnaireType = questionnaireType == null ? null : questionnaireType.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction == null ? null : introduction.trim();
    }

    public String getCreateStatus() {
        return createStatus;
    }

    public void setCreateStatus(String createStatus) {
        this.createStatus = createStatus == null ? null : createStatus.trim();
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted == null ? null : isDeleted.trim();
    }

    public Integer getGradeNo() {
        return gradeNo;
    }

    public void setGradeNo(Integer gradeNo) {
        this.gradeNo = gradeNo;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndedDate() {
        return endedDate;
    }

    public void setEndedDate(Date endedDate) {
        this.endedDate = endedDate;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear == null ? null : schoolYear.trim();
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
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

    public void setStudentnotAnswerCount(int studentnotAnswerCount) {
        this.studentNotAnswerCount = studentnotAnswerCount;
    }

}
