package com.xueduoduo.health.domain.grade;

/**
 * @author wangzhifeng
 * @date 2018年8月20日 上午11:49:53
 */
public class GradeClass {
    private Long gradeClassId;
    private int  gradeNo;
    private int  classNo;

    public int getGradeNo() {
        return gradeNo;
    }

    public void setGradeNo(int gradeNo) {
        this.gradeNo = gradeNo;
    }

    public int getClassNo() {
        return classNo;
    }

    public void setClassNo(int classNo) {
        this.classNo = classNo;
    }

    public Long getGradeClassId() {
        return gradeClassId;
    }

    public void setGradeClassId(Long gradeClassId) {
        this.gradeClassId = gradeClassId;
    }

}
