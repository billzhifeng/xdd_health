package com.xueduoduo.health.dal.dataobject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class QuestionnaireDOExample {
    protected String orderByClause;

    protected Integer offSet;

    protected Integer length;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public QuestionnaireDOExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOffSet(Integer offSet) {
        this.offSet = offSet;
    }

    public Integer getOffSet() {
        return offSet;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getLength() {
         return length;
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        protected void addCriterionForJDBCDate(String condition, Date value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            addCriterion(condition, new java.sql.Date(value.getTime()), property);
        }

        protected void addCriterionForJDBCDate(String condition, List<Date> values, String property) {
            if (values == null || values.size() == 0) {
                throw new RuntimeException("Value list for " + property + " cannot be null or empty");
            }
            List<java.sql.Date> dateList = new ArrayList<java.sql.Date>();
            Iterator<Date> iter = values.iterator();
            while (iter.hasNext()) {
                dateList.add(new java.sql.Date(iter.next().getTime()));
            }
            addCriterion(condition, dateList, property);
        }

        protected void addCriterionForJDBCDate(String condition, Date value1, Date value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            addCriterion(condition, new java.sql.Date(value1.getTime()), new java.sql.Date(value2.getTime()), property);
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andQuestionnaireTypeIsNull() {
            addCriterion("questionnaire_type is null");
            return (Criteria) this;
        }

        public Criteria andQuestionnaireTypeIsNotNull() {
            addCriterion("questionnaire_type is not null");
            return (Criteria) this;
        }

        public Criteria andQuestionnaireTypeEqualTo(String value) {
            addCriterion("questionnaire_type =", value, "questionnaireType");
            return (Criteria) this;
        }

        public Criteria andQuestionnaireTypeNotEqualTo(String value) {
            addCriterion("questionnaire_type <>", value, "questionnaireType");
            return (Criteria) this;
        }

        public Criteria andQuestionnaireTypeGreaterThan(String value) {
            addCriterion("questionnaire_type >", value, "questionnaireType");
            return (Criteria) this;
        }

        public Criteria andQuestionnaireTypeGreaterThanOrEqualTo(String value) {
            addCriterion("questionnaire_type >=", value, "questionnaireType");
            return (Criteria) this;
        }

        public Criteria andQuestionnaireTypeLessThan(String value) {
            addCriterion("questionnaire_type <", value, "questionnaireType");
            return (Criteria) this;
        }

        public Criteria andQuestionnaireTypeLessThanOrEqualTo(String value) {
            addCriterion("questionnaire_type <=", value, "questionnaireType");
            return (Criteria) this;
        }

        public Criteria andQuestionnaireTypeLike(String value) {
            addCriterion("questionnaire_type like", value, "questionnaireType");
            return (Criteria) this;
        }

        public Criteria andQuestionnaireTypeNotLike(String value) {
            addCriterion("questionnaire_type not like", value, "questionnaireType");
            return (Criteria) this;
        }

        public Criteria andQuestionnaireTypeIn(List<String> values) {
            addCriterion("questionnaire_type in", values, "questionnaireType");
            return (Criteria) this;
        }

        public Criteria andQuestionnaireTypeNotIn(List<String> values) {
            addCriterion("questionnaire_type not in", values, "questionnaireType");
            return (Criteria) this;
        }

        public Criteria andQuestionnaireTypeBetween(String value1, String value2) {
            addCriterion("questionnaire_type between", value1, value2, "questionnaireType");
            return (Criteria) this;
        }

        public Criteria andQuestionnaireTypeNotBetween(String value1, String value2) {
            addCriterion("questionnaire_type not between", value1, value2, "questionnaireType");
            return (Criteria) this;
        }

        public Criteria andTitleIsNull() {
            addCriterion("title is null");
            return (Criteria) this;
        }

        public Criteria andTitleIsNotNull() {
            addCriterion("title is not null");
            return (Criteria) this;
        }

        public Criteria andTitleEqualTo(String value) {
            addCriterion("title =", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleNotEqualTo(String value) {
            addCriterion("title <>", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleGreaterThan(String value) {
            addCriterion("title >", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleGreaterThanOrEqualTo(String value) {
            addCriterion("title >=", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleLessThan(String value) {
            addCriterion("title <", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleLessThanOrEqualTo(String value) {
            addCriterion("title <=", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleLike(String value) {
            addCriterion("title like", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleNotLike(String value) {
            addCriterion("title not like", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleIn(List<String> values) {
            addCriterion("title in", values, "title");
            return (Criteria) this;
        }

        public Criteria andTitleNotIn(List<String> values) {
            addCriterion("title not in", values, "title");
            return (Criteria) this;
        }

        public Criteria andTitleBetween(String value1, String value2) {
            addCriterion("title between", value1, value2, "title");
            return (Criteria) this;
        }

        public Criteria andTitleNotBetween(String value1, String value2) {
            addCriterion("title not between", value1, value2, "title");
            return (Criteria) this;
        }

        public Criteria andIntroductionIsNull() {
            addCriterion("introduction is null");
            return (Criteria) this;
        }

        public Criteria andIntroductionIsNotNull() {
            addCriterion("introduction is not null");
            return (Criteria) this;
        }

        public Criteria andIntroductionEqualTo(String value) {
            addCriterion("introduction =", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionNotEqualTo(String value) {
            addCriterion("introduction <>", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionGreaterThan(String value) {
            addCriterion("introduction >", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionGreaterThanOrEqualTo(String value) {
            addCriterion("introduction >=", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionLessThan(String value) {
            addCriterion("introduction <", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionLessThanOrEqualTo(String value) {
            addCriterion("introduction <=", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionLike(String value) {
            addCriterion("introduction like", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionNotLike(String value) {
            addCriterion("introduction not like", value, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionIn(List<String> values) {
            addCriterion("introduction in", values, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionNotIn(List<String> values) {
            addCriterion("introduction not in", values, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionBetween(String value1, String value2) {
            addCriterion("introduction between", value1, value2, "introduction");
            return (Criteria) this;
        }

        public Criteria andIntroductionNotBetween(String value1, String value2) {
            addCriterion("introduction not between", value1, value2, "introduction");
            return (Criteria) this;
        }

        public Criteria andCreateStatusIsNull() {
            addCriterion("create_status is null");
            return (Criteria) this;
        }

        public Criteria andCreateStatusIsNotNull() {
            addCriterion("create_status is not null");
            return (Criteria) this;
        }

        public Criteria andCreateStatusEqualTo(String value) {
            addCriterion("create_status =", value, "createStatus");
            return (Criteria) this;
        }

        public Criteria andCreateStatusNotEqualTo(String value) {
            addCriterion("create_status <>", value, "createStatus");
            return (Criteria) this;
        }

        public Criteria andCreateStatusGreaterThan(String value) {
            addCriterion("create_status >", value, "createStatus");
            return (Criteria) this;
        }

        public Criteria andCreateStatusGreaterThanOrEqualTo(String value) {
            addCriterion("create_status >=", value, "createStatus");
            return (Criteria) this;
        }

        public Criteria andCreateStatusLessThan(String value) {
            addCriterion("create_status <", value, "createStatus");
            return (Criteria) this;
        }

        public Criteria andCreateStatusLessThanOrEqualTo(String value) {
            addCriterion("create_status <=", value, "createStatus");
            return (Criteria) this;
        }

        public Criteria andCreateStatusLike(String value) {
            addCriterion("create_status like", value, "createStatus");
            return (Criteria) this;
        }

        public Criteria andCreateStatusNotLike(String value) {
            addCriterion("create_status not like", value, "createStatus");
            return (Criteria) this;
        }

        public Criteria andCreateStatusIn(List<String> values) {
            addCriterion("create_status in", values, "createStatus");
            return (Criteria) this;
        }

        public Criteria andCreateStatusNotIn(List<String> values) {
            addCriterion("create_status not in", values, "createStatus");
            return (Criteria) this;
        }

        public Criteria andCreateStatusBetween(String value1, String value2) {
            addCriterion("create_status between", value1, value2, "createStatus");
            return (Criteria) this;
        }

        public Criteria andCreateStatusNotBetween(String value1, String value2) {
            addCriterion("create_status not between", value1, value2, "createStatus");
            return (Criteria) this;
        }

        public Criteria andIsDeletedIsNull() {
            addCriterion("is_deleted is null");
            return (Criteria) this;
        }

        public Criteria andIsDeletedIsNotNull() {
            addCriterion("is_deleted is not null");
            return (Criteria) this;
        }

        public Criteria andIsDeletedEqualTo(String value) {
            addCriterion("is_deleted =", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedNotEqualTo(String value) {
            addCriterion("is_deleted <>", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedGreaterThan(String value) {
            addCriterion("is_deleted >", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedGreaterThanOrEqualTo(String value) {
            addCriterion("is_deleted >=", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedLessThan(String value) {
            addCriterion("is_deleted <", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedLessThanOrEqualTo(String value) {
            addCriterion("is_deleted <=", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedLike(String value) {
            addCriterion("is_deleted like", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedNotLike(String value) {
            addCriterion("is_deleted not like", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedIn(List<String> values) {
            addCriterion("is_deleted in", values, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedNotIn(List<String> values) {
            addCriterion("is_deleted not in", values, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedBetween(String value1, String value2) {
            addCriterion("is_deleted between", value1, value2, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedNotBetween(String value1, String value2) {
            addCriterion("is_deleted not between", value1, value2, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andGradeNoIsNull() {
            addCriterion("grade_no is null");
            return (Criteria) this;
        }

        public Criteria andGradeNoIsNotNull() {
            addCriterion("grade_no is not null");
            return (Criteria) this;
        }

        public Criteria andGradeNoEqualTo(Integer value) {
            addCriterion("grade_no =", value, "gradeNo");
            return (Criteria) this;
        }

        public Criteria andGradeNoNotEqualTo(Integer value) {
            addCriterion("grade_no <>", value, "gradeNo");
            return (Criteria) this;
        }

        public Criteria andGradeNoGreaterThan(Integer value) {
            addCriterion("grade_no >", value, "gradeNo");
            return (Criteria) this;
        }

        public Criteria andGradeNoGreaterThanOrEqualTo(Integer value) {
            addCriterion("grade_no >=", value, "gradeNo");
            return (Criteria) this;
        }

        public Criteria andGradeNoLessThan(Integer value) {
            addCriterion("grade_no <", value, "gradeNo");
            return (Criteria) this;
        }

        public Criteria andGradeNoLessThanOrEqualTo(Integer value) {
            addCriterion("grade_no <=", value, "gradeNo");
            return (Criteria) this;
        }

        public Criteria andGradeNoIn(List<Integer> values) {
            addCriterion("grade_no in", values, "gradeNo");
            return (Criteria) this;
        }

        public Criteria andGradeNoNotIn(List<Integer> values) {
            addCriterion("grade_no not in", values, "gradeNo");
            return (Criteria) this;
        }

        public Criteria andGradeNoBetween(Integer value1, Integer value2) {
            addCriterion("grade_no between", value1, value2, "gradeNo");
            return (Criteria) this;
        }

        public Criteria andGradeNoNotBetween(Integer value1, Integer value2) {
            addCriterion("grade_no not between", value1, value2, "gradeNo");
            return (Criteria) this;
        }

        public Criteria andStartDateIsNull() {
            addCriterion("start_date is null");
            return (Criteria) this;
        }

        public Criteria andStartDateIsNotNull() {
            addCriterion("start_date is not null");
            return (Criteria) this;
        }

        public Criteria andStartDateEqualTo(Date value) {
            addCriterionForJDBCDate("start_date =", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateNotEqualTo(Date value) {
            addCriterionForJDBCDate("start_date <>", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateGreaterThan(Date value) {
            addCriterionForJDBCDate("start_date >", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("start_date >=", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateLessThan(Date value) {
            addCriterionForJDBCDate("start_date <", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("start_date <=", value, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateIn(List<Date> values) {
            addCriterionForJDBCDate("start_date in", values, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateNotIn(List<Date> values) {
            addCriterionForJDBCDate("start_date not in", values, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("start_date between", value1, value2, "startDate");
            return (Criteria) this;
        }

        public Criteria andStartDateNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("start_date not between", value1, value2, "startDate");
            return (Criteria) this;
        }

        public Criteria andEndedDateIsNull() {
            addCriterion("ended_date is null");
            return (Criteria) this;
        }

        public Criteria andEndedDateIsNotNull() {
            addCriterion("ended_date is not null");
            return (Criteria) this;
        }

        public Criteria andEndedDateEqualTo(Date value) {
            addCriterionForJDBCDate("ended_date =", value, "endedDate");
            return (Criteria) this;
        }

        public Criteria andEndedDateNotEqualTo(Date value) {
            addCriterionForJDBCDate("ended_date <>", value, "endedDate");
            return (Criteria) this;
        }

        public Criteria andEndedDateGreaterThan(Date value) {
            addCriterionForJDBCDate("ended_date >", value, "endedDate");
            return (Criteria) this;
        }

        public Criteria andEndedDateGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("ended_date >=", value, "endedDate");
            return (Criteria) this;
        }

        public Criteria andEndedDateLessThan(Date value) {
            addCriterionForJDBCDate("ended_date <", value, "endedDate");
            return (Criteria) this;
        }

        public Criteria andEndedDateLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("ended_date <=", value, "endedDate");
            return (Criteria) this;
        }

        public Criteria andEndedDateIn(List<Date> values) {
            addCriterionForJDBCDate("ended_date in", values, "endedDate");
            return (Criteria) this;
        }

        public Criteria andEndedDateNotIn(List<Date> values) {
            addCriterionForJDBCDate("ended_date not in", values, "endedDate");
            return (Criteria) this;
        }

        public Criteria andEndedDateBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("ended_date between", value1, value2, "endedDate");
            return (Criteria) this;
        }

        public Criteria andEndedDateNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("ended_date not between", value1, value2, "endedDate");
            return (Criteria) this;
        }

        public Criteria andSchoolYearIsNull() {
            addCriterion("school_year is null");
            return (Criteria) this;
        }

        public Criteria andSchoolYearIsNotNull() {
            addCriterion("school_year is not null");
            return (Criteria) this;
        }

        public Criteria andSchoolYearEqualTo(String value) {
            addCriterion("school_year =", value, "schoolYear");
            return (Criteria) this;
        }

        public Criteria andSchoolYearNotEqualTo(String value) {
            addCriterion("school_year <>", value, "schoolYear");
            return (Criteria) this;
        }

        public Criteria andSchoolYearGreaterThan(String value) {
            addCriterion("school_year >", value, "schoolYear");
            return (Criteria) this;
        }

        public Criteria andSchoolYearGreaterThanOrEqualTo(String value) {
            addCriterion("school_year >=", value, "schoolYear");
            return (Criteria) this;
        }

        public Criteria andSchoolYearLessThan(String value) {
            addCriterion("school_year <", value, "schoolYear");
            return (Criteria) this;
        }

        public Criteria andSchoolYearLessThanOrEqualTo(String value) {
            addCriterion("school_year <=", value, "schoolYear");
            return (Criteria) this;
        }

        public Criteria andSchoolYearLike(String value) {
            addCriterion("school_year like", value, "schoolYear");
            return (Criteria) this;
        }

        public Criteria andSchoolYearNotLike(String value) {
            addCriterion("school_year not like", value, "schoolYear");
            return (Criteria) this;
        }

        public Criteria andSchoolYearIn(List<String> values) {
            addCriterion("school_year in", values, "schoolYear");
            return (Criteria) this;
        }

        public Criteria andSchoolYearNotIn(List<String> values) {
            addCriterion("school_year not in", values, "schoolYear");
            return (Criteria) this;
        }

        public Criteria andSchoolYearBetween(String value1, String value2) {
            addCriterion("school_year between", value1, value2, "schoolYear");
            return (Criteria) this;
        }

        public Criteria andSchoolYearNotBetween(String value1, String value2) {
            addCriterion("school_year not between", value1, value2, "schoolYear");
            return (Criteria) this;
        }

        public Criteria andCountIsNull() {
            addCriterion("count is null");
            return (Criteria) this;
        }

        public Criteria andCountIsNotNull() {
            addCriterion("count is not null");
            return (Criteria) this;
        }

        public Criteria andCountEqualTo(Integer value) {
            addCriterion("count =", value, "count");
            return (Criteria) this;
        }

        public Criteria andCountNotEqualTo(Integer value) {
            addCriterion("count <>", value, "count");
            return (Criteria) this;
        }

        public Criteria andCountGreaterThan(Integer value) {
            addCriterion("count >", value, "count");
            return (Criteria) this;
        }

        public Criteria andCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("count >=", value, "count");
            return (Criteria) this;
        }

        public Criteria andCountLessThan(Integer value) {
            addCriterion("count <", value, "count");
            return (Criteria) this;
        }

        public Criteria andCountLessThanOrEqualTo(Integer value) {
            addCriterion("count <=", value, "count");
            return (Criteria) this;
        }

        public Criteria andCountIn(List<Integer> values) {
            addCriterion("count in", values, "count");
            return (Criteria) this;
        }

        public Criteria andCountNotIn(List<Integer> values) {
            addCriterion("count not in", values, "count");
            return (Criteria) this;
        }

        public Criteria andCountBetween(Integer value1, Integer value2) {
            addCriterion("count between", value1, value2, "count");
            return (Criteria) this;
        }

        public Criteria andCountNotBetween(Integer value1, Integer value2) {
            addCriterion("count not between", value1, value2, "count");
            return (Criteria) this;
        }

        public Criteria andAdditionIsNull() {
            addCriterion("addition is null");
            return (Criteria) this;
        }

        public Criteria andAdditionIsNotNull() {
            addCriterion("addition is not null");
            return (Criteria) this;
        }

        public Criteria andAdditionEqualTo(String value) {
            addCriterion("addition =", value, "addition");
            return (Criteria) this;
        }

        public Criteria andAdditionNotEqualTo(String value) {
            addCriterion("addition <>", value, "addition");
            return (Criteria) this;
        }

        public Criteria andAdditionGreaterThan(String value) {
            addCriterion("addition >", value, "addition");
            return (Criteria) this;
        }

        public Criteria andAdditionGreaterThanOrEqualTo(String value) {
            addCriterion("addition >=", value, "addition");
            return (Criteria) this;
        }

        public Criteria andAdditionLessThan(String value) {
            addCriterion("addition <", value, "addition");
            return (Criteria) this;
        }

        public Criteria andAdditionLessThanOrEqualTo(String value) {
            addCriterion("addition <=", value, "addition");
            return (Criteria) this;
        }

        public Criteria andAdditionLike(String value) {
            addCriterion("addition like", value, "addition");
            return (Criteria) this;
        }

        public Criteria andAdditionNotLike(String value) {
            addCriterion("addition not like", value, "addition");
            return (Criteria) this;
        }

        public Criteria andAdditionIn(List<String> values) {
            addCriterion("addition in", values, "addition");
            return (Criteria) this;
        }

        public Criteria andAdditionNotIn(List<String> values) {
            addCriterion("addition not in", values, "addition");
            return (Criteria) this;
        }

        public Criteria andAdditionBetween(String value1, String value2) {
            addCriterion("addition between", value1, value2, "addition");
            return (Criteria) this;
        }

        public Criteria andAdditionNotBetween(String value1, String value2) {
            addCriterion("addition not between", value1, value2, "addition");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeIsNull() {
            addCriterion("created_time is null");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeIsNotNull() {
            addCriterion("created_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeEqualTo(Date value) {
            addCriterion("created_time =", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeNotEqualTo(Date value) {
            addCriterion("created_time <>", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeGreaterThan(Date value) {
            addCriterion("created_time >", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("created_time >=", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeLessThan(Date value) {
            addCriterion("created_time <", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeLessThanOrEqualTo(Date value) {
            addCriterion("created_time <=", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeIn(List<Date> values) {
            addCriterion("created_time in", values, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeNotIn(List<Date> values) {
            addCriterion("created_time not in", values, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeBetween(Date value1, Date value2) {
            addCriterion("created_time between", value1, value2, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeNotBetween(Date value1, Date value2) {
            addCriterion("created_time not between", value1, value2, "createdTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeIsNull() {
            addCriterion("updated_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeIsNotNull() {
            addCriterion("updated_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeEqualTo(Date value) {
            addCriterion("updated_time =", value, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeNotEqualTo(Date value) {
            addCriterion("updated_time <>", value, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeGreaterThan(Date value) {
            addCriterion("updated_time >", value, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("updated_time >=", value, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeLessThan(Date value) {
            addCriterion("updated_time <", value, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeLessThanOrEqualTo(Date value) {
            addCriterion("updated_time <=", value, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeIn(List<Date> values) {
            addCriterion("updated_time in", values, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeNotIn(List<Date> values) {
            addCriterion("updated_time not in", values, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeBetween(Date value1, Date value2) {
            addCriterion("updated_time between", value1, value2, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeNotBetween(Date value1, Date value2) {
            addCriterion("updated_time not between", value1, value2, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andCreateorIsNull() {
            addCriterion("createor is null");
            return (Criteria) this;
        }

        public Criteria andCreateorIsNotNull() {
            addCriterion("createor is not null");
            return (Criteria) this;
        }

        public Criteria andCreateorEqualTo(String value) {
            addCriterion("createor =", value, "createor");
            return (Criteria) this;
        }

        public Criteria andCreateorNotEqualTo(String value) {
            addCriterion("createor <>", value, "createor");
            return (Criteria) this;
        }

        public Criteria andCreateorGreaterThan(String value) {
            addCriterion("createor >", value, "createor");
            return (Criteria) this;
        }

        public Criteria andCreateorGreaterThanOrEqualTo(String value) {
            addCriterion("createor >=", value, "createor");
            return (Criteria) this;
        }

        public Criteria andCreateorLessThan(String value) {
            addCriterion("createor <", value, "createor");
            return (Criteria) this;
        }

        public Criteria andCreateorLessThanOrEqualTo(String value) {
            addCriterion("createor <=", value, "createor");
            return (Criteria) this;
        }

        public Criteria andCreateorLike(String value) {
            addCriterion("createor like", value, "createor");
            return (Criteria) this;
        }

        public Criteria andCreateorNotLike(String value) {
            addCriterion("createor not like", value, "createor");
            return (Criteria) this;
        }

        public Criteria andCreateorIn(List<String> values) {
            addCriterion("createor in", values, "createor");
            return (Criteria) this;
        }

        public Criteria andCreateorNotIn(List<String> values) {
            addCriterion("createor not in", values, "createor");
            return (Criteria) this;
        }

        public Criteria andCreateorBetween(String value1, String value2) {
            addCriterion("createor between", value1, value2, "createor");
            return (Criteria) this;
        }

        public Criteria andCreateorNotBetween(String value1, String value2) {
            addCriterion("createor not between", value1, value2, "createor");
            return (Criteria) this;
        }

        public Criteria andQuestionnaireTypeLikeInsensitive(String value) {
            addCriterion("upper(questionnaire_type) like", value.toUpperCase(), "questionnaireType");
            return (Criteria) this;
        }

        public Criteria andTitleLikeInsensitive(String value) {
            addCriterion("upper(title) like", value.toUpperCase(), "title");
            return (Criteria) this;
        }

        public Criteria andIntroductionLikeInsensitive(String value) {
            addCriterion("upper(introduction) like", value.toUpperCase(), "introduction");
            return (Criteria) this;
        }

        public Criteria andCreateStatusLikeInsensitive(String value) {
            addCriterion("upper(create_status) like", value.toUpperCase(), "createStatus");
            return (Criteria) this;
        }

        public Criteria andIsDeletedLikeInsensitive(String value) {
            addCriterion("upper(is_deleted) like", value.toUpperCase(), "isDeleted");
            return (Criteria) this;
        }

        public Criteria andSchoolYearLikeInsensitive(String value) {
            addCriterion("upper(school_year) like", value.toUpperCase(), "schoolYear");
            return (Criteria) this;
        }

        public Criteria andAdditionLikeInsensitive(String value) {
            addCriterion("upper(addition) like", value.toUpperCase(), "addition");
            return (Criteria) this;
        }

        public Criteria andCreateorLikeInsensitive(String value) {
            addCriterion("upper(createor) like", value.toUpperCase(), "createor");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}