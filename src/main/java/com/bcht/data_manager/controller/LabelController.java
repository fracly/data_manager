package com.bcht.data_manager.controller;

import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.entity.Label;
import com.bcht.data_manager.entity.User;
import com.bcht.data_manager.enums.Status;
import com.bcht.data_manager.service.LabelService;
import com.bcht.data_manager.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/label")
public class LabelController extends BaseController {
    public static final Logger logger = LoggerFactory.getLogger(LabelController.class);

    @Autowired
    public LabelService labelService;

    @GetMapping("/list-tree")
    @ResponseBody
    public Result listTree(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, String searchVal) {
        logger.info("user {}, query label tree list using name {}", loginUser.getUsername(), searchVal);
        Result result = new Result();
        List<Label> labelList = labelService.list(loginUser.getId(), searchVal);
        Map<String, List> resultMap = new HashMap();
        for(Label label : labelList) {
            String firstChar = label.getName().substring(0, 1);
            if(resultMap.containsKey(firstChar)){
                resultMap.get(firstChar).add(label);
            } else {
                List<Label> subList = new ArrayList<>();
                subList.add(label);
                resultMap.put(firstChar, subList);
            }
        }
        result.setData(resultMap);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    @GetMapping("/list-flat")
    @ResponseBody
    public Result listFlat(@RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        logger.info("user {}, query flat label list ", loginUser.getUsername());
        Result result = new Result();
        List<Label> labelList = labelService.list(loginUser.getId(), null);
        result.setData(labelList);
        putMsg(result, Status.SUCCESS);
        return result;
    }


    @PostMapping("/create")
    @ResponseBody
    public Result create(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, String name) {
        logger.info("user {} is creating label using name {}", loginUser.getUsername(), name);
        Result result = new Result();
        boolean isSuccess = labelService.create(loginUser.getId(), name);
        if(isSuccess) {
            putMsg(result, Status.SUCCESS);
        } else {
            putMsg(result, Status.FAILED);
        }
        return result;
    }

    @GetMapping("/delete")
    @ResponseBody
    public Result delete(int labelId) {
        logger.info("deleting label using id {}", labelId);
        Result result = new Result();
        int count = labelService.queryDataCountById(labelId);
        if(count > 0) {
            putMsg(result, Status.CUSTOM_FAILED, "不可删除正在使用的标签");
            return result;
        }
        boolean isSuccess = labelService.delete(labelId);

        if(isSuccess) {
            putMsg(result, Status.CUSTOM_SUCESSS, "删除标签成功");
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "删除标签失败");
        }
        return result;
    }

    @PostMapping("/update")
    @ResponseBody
    public Result update(int labelId, String name) {
        logger.info("updating label using name {} and id {}", name, labelId);
        Result result = new Result();
        boolean isSuccess = labelService.update(labelId, name);
        if(isSuccess) {
            putMsg(result, Status.SUCCESS);
        } else {
            putMsg(result, Status.FAILED);
        }
        return result;
    }

    @GetMapping("/queryById")
    @ResponseBody
    public Result queryById(int labelId) {
        logger.info("query label using id {} ", labelId);
        Result result = new Result();
        Label label = labelService.queryById(labelId);
        result.setData(label);
        putMsg(result, Status.SUCCESS);
        return result;
    }


    @GetMapping("/top10")
    @ResponseBody
    public Result top10(@RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        logger.info("query top 10 label using creatorId {} ", loginUser.getId());
        Result result = new Result();
        List<Label> labelList = labelService.top10(loginUser.getId());
        result.setData(labelList);
        putMsg(result, Status.SUCCESS);
        return result;
    }

}
