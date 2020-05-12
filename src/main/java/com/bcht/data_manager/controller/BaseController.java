package com.bcht.data_manager.controller;

import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.enums.Status;
import com.bcht.data_manager.utils.Result;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.Map;


/**
 * 基础 Controller
 *
 * @author fracly
 * @date 2020-05-12 17:00:00
 */
public class BaseController {


    /**
     * 从请求中获取IP地址
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");

        if (StringUtils.isNotEmpty(clientIp) && !StringUtils.equalsIgnoreCase("unKnown", clientIp)) {
            int index = clientIp.indexOf(",");
            if (index != -1) {
                return clientIp.substring(0, index);
            } else {
                return clientIp;
            }
        }

        clientIp = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(clientIp) && !StringUtils.equalsIgnoreCase("unKnown", clientIp)) {
            return clientIp;
        }

        return request.getRemoteAddr();
    }

    public Result success(String msg) {
        Result result = new Result();
        result.setCode(Status.SUCCESS.getCode());
        result.setMsg(msg);

        return result;
    }

    protected void putMsg(Result result, Status status, Object... statusParams) {
        result.setCode(status.getCode());

        if (statusParams != null && statusParams.length > 0) {
            result.setMsg(MessageFormat.format(status.getMsg(), statusParams));
        } else {
            result.setMsg(status.getMsg());
        }
    }
}
