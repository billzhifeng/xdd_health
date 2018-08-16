package com.xueduoduo.health.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author wangzhifeng
 * @date 2018年8月15日 下午8:15:07
 */
public class ToDoDelete {

    public static void main(String[] args) {

        //newQuestions();

        //newAndUpdateAndDeleteQuestion();

        addLatitudeScore();
    }

    public static void addLatitudeScore() {
        JSONArray ja = new JSONArray();

        JSONObject o = new JSONObject();
        o.put("questionId", 1000040);
        o.put("latitudeId", 100000);
        JSONArray op = new JSONArray();
        JSONObject op1 = new JSONObject();
        op1.put("optionId", 1000071);
        op1.put("score", 1.2);
        op.add(op1);
        JSONObject op2 = new JSONObject();
        op2.put("optionId", 1000078);
        op2.put("score", 1.5);
        op.add(op2);
        o.put("optionsStr", op);
        //ja.add(o);

        JSONObject o3 = new JSONObject();
        o3.put("questionId", 1000043);
        o3.put("latitudeId", 100001);
        JSONArray op3 = new JSONArray();
        JSONObject op31 = new JSONObject();
        op31.put("optionId", 1000079);
        op31.put("score", 1.9);
        op3.add(op31);
        JSONObject op32 = new JSONObject();
        op32.put("optionId", 1000080);
        op32.put("score", 1.4);
        op3.add(op32);
        o3.put("optionsStr", op3);
        ja.add(o3);

        System.out.println(ja.toJSONString());
    }

    public static void newAndUpdateAndDeleteQuestion() {
        //第一个题目更新选项 + 增加选项 + 删除原来选项
        JSONArray ja = new JSONArray();
        JSONObject o = new JSONObject();
        o.put("questionName", "新第一个题目2");
        o.put("questionNo", 1);
        o.put("questionAudioUrl", "questionAudioUrl");
        o.put("questionId", 1000040);

        //修改选项1
        JSONArray ops = new JSONArray();
        JSONObject op11 = new JSONObject();
        op11.put("optionNo", 1);
        op11.put("optionName", "选项111111");
        op11.put("optionId", 1000071);
        ops.add(op11);

        //删除op12  optionId=1000072  1000075

        //增加选项
        JSONObject op13 = new JSONObject();
        op13.put("optionNo", 2);
        op13.put("optionName", "新选项2");
        ops.add(op13);

        o.put("optionsStr", ops);

        ja.add(o);
        //--

        //第二个题目 被删除 id=1000042
        //选项 1000076 、 1000077 被删除

        //--
        //添加第三个题目
        JSONObject o3 = new JSONObject();
        o3.put("questionName", "新增个题目22");
        o3.put("questionNo", 2);
        o3.put("questionAudioUrl", "questionAudioUrl22");

        JSONArray ops3 = new JSONArray();
        JSONObject op31 = new JSONObject();
        op31.put("optionNo", 1);
        op31.put("optionName", "选项11");
        ops3.add(op31);

        JSONObject op32 = new JSONObject();
        op32.put("optionNo", 2);
        op32.put("optionName", "选项22");
        ops3.add(op32);

        o3.put("optionsStr", ops3);

        ja.add(o3);

        System.out.println(ja.toJSONString());
    }

    public static void newQuestions() {
        JSONArray ja = new JSONArray();
        JSONObject o = new JSONObject();
        o.put("questionName", "第一个题目");
        o.put("questionNo", 1);
        o.put("questionAudioUrl", "audioUrl");

        JSONArray ops = new JSONArray();
        JSONObject op11 = new JSONObject();
        op11.put("optionNo", 1);
        op11.put("optionName", "选项1");
        ops.add(op11);
        JSONObject op12 = new JSONObject();
        op12.put("optionNo", 2);
        op12.put("optionName", "选项2");
        ops.add(op12);

        o.put("optionsStr", ops);

        ja.add(o);
        //
        JSONObject o2 = new JSONObject();
        o2.put("questionName", "第二个题目");
        o2.put("questionNo", 2);
        o2.put("questionAudioUrl", "audioUrl22");

        JSONArray ops2 = new JSONArray();
        JSONObject op21 = new JSONObject();
        op21.put("optionNo", 1);
        op21.put("optionName", "选项21");
        ops2.add(op21);
        JSONObject op22 = new JSONObject();
        op22.put("optionNo", 2);
        op22.put("optionName", "选项22");
        ops2.add(op22);

        o2.put("optionsStr", ops2);

        ja.add(o2);

        System.out.println(ja.toJSONString());
    }
}
