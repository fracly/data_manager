package com.bcht.data_manager.controller;

import com.bcht.data_manager.entity.ClouderaEntity;
import com.bcht.data_manager.enums.Status;
import com.bcht.data_manager.service.AnalysisService;
import com.bcht.data_manager.service.ClouderaManagerMetricsService;
import com.bcht.data_manager.service.DataService;
import com.bcht.data_manager.service.DataSourceService;
import com.bcht.data_manager.utils.DateUtils;
import com.bcht.data_manager.utils.ExcelUtils;
import com.bcht.data_manager.utils.FileUtils;
import com.bcht.data_manager.utils.Result;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
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

    @GetMapping("/load")
    public Result load() {
        logger.info("querying data manager system load");
        Result result = new Result();
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
     * 每日活跃情况
     */
    @GetMapping("/user-active")
    public Result userActive(@Param("startDate") String startDate, @Param("endDate") String endDate) {
        logger.info("querying user active statistic");
        List<Map<String, Object>> resultList = analysisService.userActive(startDate, endDate);
        Result result = new Result();
        result.setData(resultList);
        putMsg(result, Status.SUCCESS);
        return result;
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
    public Result countByLabel(String startDate, String endDate, Integer limit) {
        logger.info("querying label's data count between {} and {} limit {}", startDate, endDate, limit);
        if (limit == null) {
            limit = 10;
        }
        return dataService.countByLabel(startDate, endDate, limit);
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
        if (type == 1) {
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

        for (Map<String, Object> m : resultList) {
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

    /**
     * 每日登录用户量
     */
    @GetMapping("/login-user-by-day")
    public Result loginUserByDay() {
        Result result = new Result();
        String endDateTime = DateUtils.getDateTime();
        Date date = DateUtils.nextDay(-30);
        String startDateTime = DateUtils.formatDate(date, DateUtils.DATETIME_FORMAT);
        List<Map<String, Object>> resultList = analysisService.loginCountByDay(startDateTime, endDateTime);
        result.setData(resultList);
        return result;
    }

    /**
     * cloudera manager api
     **/
    @GetMapping("/load/trend")
    public Result getLoadTrendFromClouderaApi(Integer type) {
        logger.info("statistic data from cloudera manager api ...");
        Result result = new Result();
        List<ClouderaEntity> list = ClouderaManagerMetricsService.getClusterLoadInfo(type);
        result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    @GetMapping("/load/realtime")
    public Result getLoadRealTimeFromClouderaApi() {
        logger.info("get realtime data from cloudera manager api ...");
        Result result = new Result();
        Map<String, Object> realTimeMap = ClouderaManagerMetricsService.getClusterRealTimeInfo();
        result.setData(realTimeMap);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * 报表下载
     */
    @GetMapping("/report/download")
    public void getReportDownload(HttpServletResponse response, String startDate, String endDate) {
        Result result = new Result();
        //首先获取新增数据
        List<Map<String, Object>> resultList = analysisService.report(startDate + " 00:00:00", endDate + " 23:59:59");

        // 将输出结果写入Excel
        String path = FileUtils.getDownloadFilename("report-" + new Date().getTime() + ".xlsx");

        HSSFWorkbook workbook = new HSSFWorkbook(); //创建HSSFWorkbook对象

        // 1.生成字体对象
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("新宋体");
        font.setColor((short) 64);

        // 2.生成样式对象
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER_SELECTION);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        cellStyle.setFont(font);

        // 3.生成sheet
        HSSFSheet sheet = workbook.createSheet("每日报表统计");
        sheet.setColumnWidth((short) 0, 2500);
        sheet.setColumnWidth((short) 1, 4000);
        sheet.setColumnWidth((short) 2, 4500);
        sheet.setColumnWidth((short) 3, 4500);
        sheet.setColumnWidth((short) 4, 4500);
        HSSFRow row0 = sheet.createRow(0);

        ExcelUtils.getCell(row0, 0, "系统每日活跃情况统计", cellStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

        HSSFRow row2 = ExcelUtils.getRow(sheet, 1, cellStyle);
        String[] cellList2 = new String[]{"日期", "数据新增数量", "每日用户登陆次数", "每日数据下载次数", "每日数据检索次数"};
        //创建 第二行 cell
        for(int i = 0; i < 5; i ++) {
            HSSFCell cell = row2.createCell(i);
            cell.setCellValue(cellList2[i]);
        }

        for (int i = 3; i < resultList.size() + 3 ; i++) {
            Map<String, Object> report = resultList.get(i - 3);
            HSSFRow row = ExcelUtils.getRow(sheet, i, null);

            HSSFCell cell1 = row.createCell(0);
            cell1.setCellValue(report.get("day").toString());
            HSSFCell cell2 = row.createCell(1);
            cell2.setCellValue(report.get("increase").toString());
            HSSFCell cell3 = row.createCell(2);
            cell3.setCellValue(report.get("login").toString());
            HSSFCell cell4 = row.createCell(3);
            cell4.setCellValue(report.get("download").toString());
            HSSFCell cell5 = row.createCell(4);
            cell5.setCellValue(report.get("search").toString());
        }

        try {
            response.setHeader("Content-Disposition", "attachment;filename*= UTF-8''" + URLEncoder.encode("每日报表统计" + DateUtils.formatDate(new Date()) + ".xls", "UTF-8"));
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
