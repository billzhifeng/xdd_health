package com.xueduoduo.health.domain.user;

import java.util.Date;

/**
 * @author wangzhifeng
 * @date 2018年8月10日 下午10:42:29
 */
public class User {
    private Long    id;

    private String  role;

    private String  loginName;

    private String  userName;

    private String  position;

    private String  gender;

    private String  accountNo;

    private String  studentNo;

    private Integer gradeNo;

    private Integer classNo;

    private String  password;

    private String  phone;

    private String  userStatus;

    private String  addition;

    private String  createorId;

    private Date    createdTime;

    private Date    updatedTime;

    private String  isDeleted;

    private String  headerImg;

    /**
     * 已经测评数量
     */
    private int     studentReportCount;

    private boolean isReport;

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

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
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

}
