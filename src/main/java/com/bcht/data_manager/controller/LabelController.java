package com.bcht.data_manager.controller;

import com.bcht.data_manager.service.LabelService;
import com.bcht.data_manager.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:标签管理
 * @author: jgn
 * @Date: 2020/4/16
 * @version:
 */
@RestController
@RequestMapping("/api/label")
public class LabelController extends BaseController {
    public static final Logger logger = LoggerFactory.getLogger(LabelController.class);

    @Autowired
    public LabelService labelService;

    /**
     * 获取标签列表
     */
    @GetMapping("/list")
    @ResponseBody
    public Result queryList() {
        return labelService.queryList();
    }

}
