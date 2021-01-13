package com.bcht.data_manager.service;


import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.controller.BaseController;
import com.bcht.data_manager.entity.Session;
import com.bcht.data_manager.entity.User;
import com.bcht.data_manager.mapper.SessionMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

/**
 * session service
 */
@Service
public class SessionService extends BaseService{

    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);

    @Autowired
    private SessionMapper sessionMapper;

    /**
     * get user session from request
     *
     * @param request
     * @return
     */
    public Session getSession(HttpServletRequest request)  {
        String sessionId = request.getHeader(Constants.SESSION_ID);

        if(StringUtils.isBlank(sessionId)) {
            Cookie cookie = getCookie(request, Constants.SESSION_ID);

            if (cookie != null) {
                sessionId = cookie.getValue();
            }
        }

        if(StringUtils.isBlank(sessionId)) {
            return null;
        }

        String ip = BaseController.getClientIpAddress(request);
        logger.info("get session: {}, ip: {}", sessionId, ip);

        return sessionMapper.queryByIdAndIp(sessionId, ip);
    }

    /**
     * create session
     *
     * @param user
     * @param ip
     * @return
     */
    public String createSession(User user, String ip) {
        // logined
        Session session = sessionMapper.queryByUserIdAndIp(user.getId(), ip);
        Date now = new Date();

        /**
         * if you have logged in and are still valid, return directly
         */
        if (session != null) {
            if (now.getTime() - session.getLastLoginTime().getTime() <= Constants.SESSION_TIME_OUT * 1000) {
                /**
                 * updateProcessInstance the latest login time
                 */
                sessionMapper.update(session.getId(), now);

                return session.getId();

            } else {
                /**
                 * session expired, then delete this session first
                 */
                sessionMapper.deleteById(session.getId());
            }
        }

        // assign new session
        session = new Session();

        session.setId(UUID.randomUUID().toString());
        session.setIp(ip);
        session.setUserId(user.getId());
        session.setLastLoginTime(now);

        sessionMapper.insert(session);

        return session.getId();
    }
}
