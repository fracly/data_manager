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

/**
 * 标签Controller
 * 标签的CURD、和统计信息
 */
@RestController
@RequestMapping("/api/label")
public class LabelController extends BaseController {
    public static final Logger logger = LoggerFactory.getLogger(LabelController.class);

    @Autowired
    public LabelService labelService;

    /**
     * 新增标签
     */
    @PostMapping("/create")
    @ResponseBody
    public Result create(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, String name) {
        logger.info("user {} is creating label using name {}", loginUser.getUsername(), name);
        Result result = new Result();
        boolean isSuccess = labelService.create(loginUser.getId(), name);
        if(isSuccess) {
            putMsg(result, Status.CUSTOM_SUCESSS, "创建数据标签成功");
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "创建数据标签失败");
        }
        return result;
    }

    /**
     * 删除标签
     */
    @GetMapping("/delete")
    public Result delete(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, int labelId) {
        logger.info("deleting label using id {}", labelId);
        Result result = new Result();
        Label label = labelService.queryById(labelId);
        if(label.getCreatorId() != loginUser.getId() && !loginUser.getUsername().equals(Constants.ADMIN)) {
            putMsg(result, Status.CUSTOM_FAILED, "请不要删除他人的标签哦~");
            return result;
        }
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

    /**
     * 更新标签
     */
    @PostMapping("/update")
    public Result update(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, int id, String name) {
        logger.info("updating label using name {} and id {}", name, id);
        Result result = new Result();
        Label label = labelService.queryById(id);
        if(label.getCreatorId() != loginUser.getId() && !loginUser.getUsername().equals(Constants.ADMIN)) {
            putMsg(result, Status.CUSTOM_FAILED, "请不要修改他人的标签哦~");
            return result;
        }
        boolean isSuccess = labelService.update(id, name);
        if(isSuccess) {
            putMsg(result, Status.CUSTOM_SUCESSS, "更新数据标签成功");
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "更新数据标签失败");
        }
        return result;
    }

    /**
     * 标签查询-根据ID
     */
    @GetMapping("/queryById")
    public Result queryById(int labelId) {
        logger.info("query label using id {} ", labelId);
        Result result = new Result();
        Label label = labelService.queryById(labelId);
        result.setData(label);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * 标签查询-返回树形列表
     */
    @GetMapping("/list-tree")
    public Result listTree(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, String searchVal) {
        logger.info("user {}, query label tree list using name {}", loginUser.getUsername(), searchVal);
        Result result = new Result();
        List<Label> labelList = labelService.list(searchVal);
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

    /**
     * 标签查询-返回平行列表
     */
    @GetMapping("/list-flat")
    public Result listFlat(@RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        logger.info("user {}, query flat label list ", loginUser.getUsername());
        Result result = new Result();
        List<Label> labelList = labelService.list(null);
        result.setData(labelList);
        putMsg(result, Status.SUCCESS);
        return result;
    }

}
