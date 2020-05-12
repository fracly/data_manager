package com.bcht.data_manager.controller;

import com.bcht.data_manager.enums.Status;
import com.bcht.data_manager.service.AnalysisService;
import com.bcht.data_manager.service.DataService;
import com.bcht.data_manager.service.DataSourceService;
import com.bcht.data_manager.utils.DateUtils;
import com.bcht.data_manager.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 统计 Controller
 * 统计面板所需接口，按照数据各维度进行数据统计、分类，形成图标和表格
 *
 * @author fracly
 * @date 2020-05-12:15:50:00
 */
@RestController
@RequestMapping("/api/analysis")
public class AnalysisController extends BaseController {
    public static final Logger logger = LoggerFactory.getLogger(AnalysisController.class);

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private DataService dataService;

    @Autowired
    private DataSourceService dataSourceService;

    /**
     * 集群容量统计
     */
    @GetMapping("/capacity")
    public Result capacity() {
        logger.info("querying data manager total capacity");
        Map<String, String> res = analysisService.capacity();
        Result result = new Result();
        result.setData(res);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * 数据数量统计
     */
    @GetMapping("/data-count")
    public Result dataCount() {
        logger.info("querying data count");
        return dataService.countDataByStatus();
    }

    /**
     * 数据源数量统计
     */
    @GetMapping("/datasource-count")
    public Result datasourceCount() {
        logger.info("querying datasource count");
        Result result = new Result();
        Map<String, Long> res = dataSourceService.groupByType();
        result.setData(res);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * 数据下载统计
     */
    @GetMapping("/download-count")
    public Result downloadCount() {
        logger.info("querying download data count");
        return dataService.countDownloadByDay();
    }

    /**
     * 数据每日新增数量
     */
    @GetMapping("/increase-by-day")
    public Result increaseByDay(String startDate, String endDate) {
        logger.info("querying data increase by day between {} and {}", startDate, endDate);
        return dataService.countIncreaseByDay(startDate, endDate);
    }

    /**
     * 数据标签分布量
     */
    @GetMapping("/count-by-label")
    public Result countByLabel(String startDate, String endDate) {
        logger.info("querying label's data count between {} and {}", startDate, endDate);
        return dataService.countByLabel(startDate, endDate);
    }

    /**
     * 关键字搜索统计
     */
    @GetMapping("/search-keyword")
    public Result searchKeyword() {
        logger.info("querying recent one month search record");
        Result result = new Result();
        String endDateTime = DateUtils.getDateTime();
        Date date = DateUtils.nextDay(-30);
        String startDateTime = DateUtils.formatDate(date, DateUtils.DATETIME_FORMAT);
        List<Map<String, Object>> resulltList = analysisService.searchKeyword(startDateTime, endDateTime);
        result.setData(resulltList);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * 数据类型占比统计
     */
    @GetMapping("/data-type-percentage")
    public Result typePercentage(Integer type) {
        logger.info("querying data type percentage");
        Result result = new Result();
        String startDateTime = null;
        String endDateTime = DateUtils.getDateTime();
        if( type == 1){
            startDateTime = DateUtils.formatDate(DateUtils.nextDay(-1), DateUtils.DATETIME_FORMAT);
        } else if (type == 7) {
            startDateTime = DateUtils.formatDate(DateUtils.nextDay(-7), DateUtils.DATETIME_FORMAT);
        } else if (type == 30) {
            startDateTime = DateUtils.formatDate(DateUtils.nextDay(-30), DateUtils.DATETIME_FORMAT);
        } else if (type == 180) {
            startDateTime = DateUtils.formatDate(DateUtils.nextDay(-180), DateUtils.DATETIME_FORMAT);
        }
        List<Map<String, Object>> list = dataService.dateTypePercentage(startDateTime, endDateTime);
        result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * 搜索次数统计
     */
    @GetMapping("/search-count-by-day")
    public Result searchCountByDay() {
        Result result = new Result();
        Map<String, Object> map = new HashMap<>();

        String endDateTime = DateUtils.getDateTime();
        Date date = DateUtils.nextDay(-30);
        String startDateTime = DateUtils.formatDate(date, DateUtils.DATETIME_FORMAT);

        List<Map<String, Object>> resultList = analysisService.searchCountByDay(startDateTime, endDateTime);
        long total = 0L;

        for(Map<String, Object> m : resultList) {
            total += Long.parseLong(m.get("y").toString());
        }
        map.put("total", total);

        result.setData(resultList);
        result.setDataMap(map);
        return result;
    }

    /**
     * 搜索用户数统计
     */
    @GetMapping("/search-user-by-day")
    public Result searchUserByDay() {
        Result result = new Result();
        Map<String, Object> map = new HashMap<>();

        String endDateTime = DateUtils.getDateTime();
        Date date = DateUtils.nextDay(-30);
        String startDateTime = DateUtils.formatDate(date, DateUtils.DATETIME_FORMAT);

        List<Map<String, Object>> resultList = analysisService.searchUserByDay(startDateTime, endDateTime);
        map.put("total", analysisService.searchUserTotal(startDateTime, endDateTime));

        result.setData(resultList);
        result.setDataMap(map);
        return result;
    }

}
