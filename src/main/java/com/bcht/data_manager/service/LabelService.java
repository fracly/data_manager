package com.bcht.data_manager.service;

import com.bcht.data_manager.entity.Label;
import com.bcht.data_manager.entity.Permission;
import com.bcht.data_manager.entity.Role;
import com.bcht.data_manager.entity.User;
import com.bcht.data_manager.mapper.LabelMapper;
import com.bcht.data_manager.mapper.UserMapper;
import com.bcht.data_manager.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: jgn
 * @Date: 2020/4/16
 * @version:
 */
@Service
public class LabelService extends BaseService {
    @Autowired
    private LabelMapper labelMapper;

    public Result queryList() {
        Result result = new Result<>();
        return result;
    }
}
