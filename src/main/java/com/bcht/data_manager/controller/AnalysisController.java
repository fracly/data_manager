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

import java.util.Date;
import java.util.List;
import java.util.Map;


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
     * query system capacity
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
     * query data count
     */
    @GetMapping("/data-count")
    public Result dataCount() {
        logger.info("querying data count");
        return dataService.countDataByStatus();
    }

    /**
     * query datasource count
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
     * query data download count
     */
    @GetMapping("/download-count")
    public Result downloadCount() {
        logger.info("querying download data count");


        return dataService.countDownloadByDay();
    }

    /**
     * query data increase count by day
     */
    @GetMapping("/increase-by-day")
    public Result increaseByDay(String startDate, String endDate) {
        return dataService.countIncreaseByDay(startDate, endDate);
    }

    /**
     * query data increase count by day
     */
    @GetMapping("/count-by-label")
    public Result countByLabel(String startDate, String endDate) {
        return dataService.countByLabel(startDate, endDate);
    }

    /**
     * query data increase count by day
     */
    @GetMapping("/search-keyword")
    public Result searchKeyword() {
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
     * query data type percentage pie
     */
    @GetMapping("/data-type-percentage")
    public Result typePercentage(Integer type) {
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
        List<Map<String, Object>> list = analysisService.dateTypePercentage(startDateTime, endDateTime);
        result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

}
