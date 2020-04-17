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

import java.util.List;

@RestController
@RequestMapping("/api/label")
public class LabelController extends BaseController {
    public static final Logger logger = LoggerFactory.getLogger(LabelController.class);

    @Autowired
    public LabelService labelService;

    @GetMapping("/list")
    @ResponseBody
    public Result list(@RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        Result result = new Result();
        List<Label> labelList = labelService.list(loginUser.getId());
        result.setData(labelList);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    @PostMapping("/create")
    @ResponseBody
    public Result create(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, String name) {
        Result result = new Result();
        boolean isSuccess = labelService.create(loginUser.getId(), name);
        if(isSuccess) {
            putMsg(result, Status.SUCCESS);
        } else {
            putMsg(result, Status.FAILED);
        }
        return result;
    }

    /**
     * 删除标签
     */
    @GetMapping("/delete")
    @ResponseBody
    public Result delete(int labelId) {
        Result result = new Result();
        boolean isSuccess = labelService.delete(labelId);
        if(isSuccess) {
            putMsg(result, Status.SUCCESS);
        } else {
            putMsg(result, Status.FAILED);
        }
        return result;
    }

    @PostMapping("/update")
    @ResponseBody
    public Result update(int labelId, String name) {
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
        Result result = new Result();
        Label label = labelService.queryById(labelId);
        result.setData(label);
        putMsg(result, Status.SUCCESS);
        return result;
    }

}
