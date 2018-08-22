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

        //addLatitudeScore();

        addTeacherTestQuestionnaire();
    }

    public static void addTeacherTestQuestionnaire() {
        JSONArray ja = new JSONArray();
        JSONObject o = new JSONObject();
        o.put("questionId", 1000048);
        o.put("optionId", 1000091);
        o.put("latitudeId", 100000);
        o.put("optionNo", 1);
        ja.add(o);

        JSONObject o2 = new JSONObject();
        o2.put("questionId", 1000049);
        o2.put("optionId", 1000093);
        o2.put("latitudeId", 100001);
        o2.put("optionNo", 1);
        ja.add(o2);

        JSONObject o3 = new JSONObject();
        o3.put("questionId", 1000050);
        o3.put("optionId", 1000096);
        o3.put("latitudeId", 100004);
        o3.put("optionNo", 2);
        ja.add(o3);

        JSONObject o4 = new JSONObject();
        o4.put("questionId", 1000051);
        o4.put("optionId", 1000098);
        o4.put("latitudeId", 100005);
        o4.put("optionNo", 2);
        ja.add(o4);

        JSONObject o5 = new JSONObject();
        o5.put("questionId", 1000052);
        o5.put("optionId", 1000100);
        o5.put("latitudeId", 100006);
        o5.put("optionNo", 2);
        ja.add(o5);

        System.out.println(ja.toJSONString());
    }

    public static void addLatitudeScore() {
        JSONArray ja = new JSONArray();
        JSONObject o = new JSONObject();
        o.put("latitudeId", 100000);
        o.put("scoreId", 1);
        o.put("minScore", 1.0);
        o.put("maxScore", 1.4);
        o.put("desc",
                "待人较为宽容，不与同学斤斤计较；能严格遵守各项规章制度，积极参加各项体育活动；有较明确的学习目标，能专心听课，自觉完成各项练习，希今后，注意培养良好的学习方法，遇事多与老师、同学交流。期待下学期更上一层楼！");

        ja.add(o);

        JSONObject o2 = new JSONObject();
        o2.put("latitudeId", 100000);
        //        o2.put("scoreId", 1000071);
        o2.put("minScore", 1.5);
        o2.put("maxScore", 2.0);
        o2.put("desc",
                "生活中的你时而活泼开朗，时而恬静文雅，是一个有礼貌的女孩。尊敬师长，团结同学，遵守校规校纪，积极参与学校组织的各项活动，舞蹈方面的特长为你赢得了观众的喝彩，同样也在同学中树立了你的形象。你是一个多才多艺的女孩，音体美样样精通，但作为一名中学生，最重要的是搞好学习，在这一点上你做得很不够");

        //ja.add(o2);

        JSONObject o21 = new JSONObject();
        o21.put("latitudeId", 100000);
        //        o2.put("scoreId", 1000071);
        o21.put("minScore", 2.1);
        o21.put("maxScore", 2.5);
        o21.put("desc",
                "22生活中的你时而活泼开朗，时而恬静文雅，是一个有礼貌的女孩。尊敬师长，团结同学，遵守校规校纪，积极参与学校组织的各项活动，舞蹈方面的特长为你赢得了观众的喝彩，同样也在同学中树立了你的形象。你是一个多才多艺的女孩，音体美样样精通，但作为一名中学生，最重要的是搞好学习，在这一点上你做得很不够");

        //ja.add(o21);
        //--

        JSONObject o3 = new JSONObject();
        o3.put("latitudeId", 100001);
        o3.put("scoreId", 3);
        o3.put("minScore", 1.0);
        o3.put("maxScore", 1.6);
        o3.put("desc",
                "聪明，善于开动脑筋，勇于探索，富有进取心的好学生。以顽强的意志力和拼搏精神感染着每一个同学，不愧是班级同学的榜样。拼搏、追求、奋斗，是你学业成功的保证。你有优秀的思维素质和能力，学习刻苦，成绩优秀。");

        ja.add(o3);

        JSONObject o4 = new JSONObject();
        o4.put("latitudeId", 100001);
        o4.put("scoreId", 4);
        o4.put("minScore", 1.7);
        o4.put("maxScore", 2.2);
        o4.put("desc",
                "成功的捷径就在自己的身边，那就是辛勤劳动，勤于积累，脚踏实地地学习。当你静下心来认真读书的时候，你就发现了你得以成功的途径就是一个平静的心态面对一切。在学习中，希望你能够把握好自己的优势科目，由这些科目带动你的弱项，从而达到你所希望的目标。也希望你今后的学习和生活中更加积极和大胆一些，，因为你也同样优秀.！");
        //ja.add(o4);

        JSONObject o5 = new JSONObject();
        o5.put("latitudeId", 100001);
        //        o5.put("scoreId", 4);
        o5.put("minScore", 2.7);
        o5.put("maxScore", 3.2);
        o5.put("desc",
                "成功的捷径就在自己的身边，那就是辛勤劳动，勤于积累，脚踏实地地学习。当你静下心来认真读书的时候，你就发现了你得以成功的途径就是一个平静的心态面对一切。在学习中，希望你能够把握好自己的优势科目，由这些科目带动你的弱项，从而达到你所希望的目标。也希望你今后的学习和生活中更加积极和大胆一些，，因为你也同样优秀.！");
        //ja.add(o5);

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
