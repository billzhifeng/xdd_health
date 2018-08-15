package com.xueduoduo.health.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author wangzhifeng
 * @date 2018年8月15日 下午8:15:07
 */
public class ToDoDelete {

    public static void main(String[] args) {
        JSONArray ja = new JSONArray();
        JSONObject o = new JSONObject();
        o.put("questionName", "第一个题目");
        o.put("questionNo", 1);
        o.put("audioUrl", "audioUrl");

        JSONArray ops = new JSONArray();
        JSONObject op = new JSONObject();
        op.put("optionNo", 1);
        op.put("displayName", "选项1");
        ops.add(op);
        JSONObject op2 = new JSONObject();
        op2.put("optionNo", 2);
        op2.put("displayName", "选项2");
        ops.add(op2);

        o.put("optionsStr", ops);

        ja.add(o);
        //
        JSONObject o2 = new JSONObject();
        o2.put("questionName", "第二个题目");
        o2.put("questionNo", 2);
        o2.put("audioUrl", "audioUrl22");

        JSONArray ops2 = new JSONArray();
        JSONObject op22 = new JSONObject();
        op22.put("optionNo", 1);
        op22.put("displayName", "选项21");
        ops2.add(op22);
        JSONObject op23 = new JSONObject();
        op23.put("optionNo", 2);
        op23.put("displayName", "选项22");
        ops2.add(op23);

        o2.put("optionsStr", ops2);

        ja.add(o2);

        System.out.println(ja.toJSONString());
    }
}
