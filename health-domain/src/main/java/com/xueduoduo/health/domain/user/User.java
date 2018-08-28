package com.xueduoduo.health.domain.user;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.github.java.common.base.Printable;
import com.xueduoduo.health.domain.grade.GradeClass;

/**
 * @author wangzhifeng
 * @date 2018年8月10日 下午10:42:29
 */
public class User extends Printable {
    private Long             id;

    private String           role;

    //    private String           loginName; 废弃

    private String           userName;

    private String           position;

    private String           positionStr;

    private String           gender;

    private String           accountNo;

    private String           studentNo;

    private Integer          gradeNo;

    private Integer          classNo;

    private String           password;

    private String           phone;

    private String           userStatus;

    private String           addition;

    private String           createorId;

    private Date             createdTime;

    private Date             updatedTime;

    private String           isDeleted;

    private String           headerImg;

    private List<GradeClass> gradeClassList;

    private JSONArray        gradeClasses;
    /**
     * 已经测评数量(每学年一次)
     */
    private int              studentReportCount;
    /**
     * 已经测评数量(每学年一次)
     */
    private boolean          isReport;

    public boolean isReport() {
        if (this.getStudentReportCount() > 0) {
            isReport = true;
        }
        return isReport;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public Integer getGradeNo() {
        return gradeNo;
    }

    public void setGradeNo(Integer gradeNo) {
        this.gradeNo = gradeNo;
    }

    public Integer getClassNo() {
        return classNo;
    }

    public void setClassNo(Integer classNo) {
        this.classNo = classNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getAddition() {
        return addition;
    }

    public void setAddition(String addition) {
        this.addition = addition;
    }

    public String getCreateorId() {
        return createorId;
    }

    public void setCreateorId(String createorId) {
        this.createorId = createorId;
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

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getHeaderImg() {
        return headerImg;
    }

    public void setHeaderImg(String headerImg) {
        this.headerImg = headerImg;
    }

    public int getStudentReportCount() {
        return studentReportCount;
    }

    public void setStudentReportCount(int studentReportCount) {
        this.studentReportCount = studentReportCount;
    }

    public List<GradeClass> getGradeClassList() {
        return gradeClassList;
    }

    public void setGradeClassList(List<GradeClass> gradeClassList) {
        this.gradeClassList = gradeClassList;
    }

    public void setReport(boolean isReport) {
        this.isReport = isReport;
    }

    public JSONArray getGradeClasses() {
        return gradeClasses;
    }

    public void setGradeClasses(JSONArray gradeClasses) {
        this.gradeClasses = gradeClasses;
    }

    public String getPositionStr() {
        return positionStr;
    }

    public void setPositionStr(String positionStr) {
        this.positionStr = positionStr;
    }

}
