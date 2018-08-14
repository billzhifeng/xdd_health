package com.xueduoduo.health.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * TODO 测试代码 等待删除
 * 
 * @author wangzhifeng
 */
@Controller
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @RequestMapping(value = "/latitude", method = RequestMethod.GET)
    public String latitude() {
        return "/latitude";
    }

}
