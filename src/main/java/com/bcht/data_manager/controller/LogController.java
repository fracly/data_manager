package com.bcht.data_manager.controller;

import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.entity.*;
import com.bcht.data_manager.enums.DbType;
import com.bcht.data_manager.enums.Status;
import com.bcht.data_manager.service.DataService;
import com.bcht.data_manager.service.DataSourceService;
import com.bcht.data_manager.service.LabelService;
import com.bcht.data_manager.service.LogService;
import com.bcht.data_manager.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;


/**
 * 日志 Controller
 * 对应日志的查询操作，日志分为3类
 *      1、登陆日志
 *      2、下载日志
 *      3、查询日志
 *
 *  @author fracly
 *  @date 2020-06-08 08:53:00
 */
@RestController
@RequestMapping("/api/log")
public class LogController extends BaseController {
    public static final Logger logger = LoggerFactory.getLogger(LogController.class);

    @Autowired
    private LogService logService;

    /**
     * 登陆日志
     */
    @GetMapping("/login")
    public Result login(String name, String startTime, String endTime, Integer pageNo, Integer pageSize){
        return logService.loginLog(name, startTime, endTime, pageNo, pageSize);
    }

    /**
     * 导出登陆日志
     */
    @GetMapping("/login/export")
    @ResponseBody
    public ResponseEntity loginLogExport(String name, String startTime, String endTime) {
        // first line
        String firstLine = "序号|姓名|账号|登陆时间|登陆IP";
        Result result = logService.loginLog(name, startTime, endTime, 1, 1000);
        List<LoginRecord> records = (List<LoginRecord>)result.getData();
        List<String> lineList = new ArrayList<>();
        lineList.add(firstLine);
        for(LoginRecord loginRecord : records) {
            lineList.add(loginRecord.toString());
        }
        String localFileName = FileUtils.getDownloadFilename(new Date().getTime() +"-login-log.csv");
        File localFile =  new File(localFileName);
        if(!localFile.exists()) {
            localFile.getParentFile().mkdirs();
        }
        try{
            FileWriter fw = new FileWriter(localFileName);
            for(int i = 0; i < lineList.size(); i ++) {
                fw.write(lineList.get(i) + "\n");
            }
            fw.flush();
            fw.close();
        }catch (IOException e) {
            logger.error("查询数据写入本地文件失败" + e.getMessage());
            e.printStackTrace();
        }
        try {
            org.springframework.core.io.Resource file = FileUtils.file2Resource(localFileName);
            if(file == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("数据文件下载失败");
            }
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(file.getFilename(),"UTF-8") + "\"")
                    .body(file);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("文件下载出错");
        }
    }

    /**
     * 下载日志
     */
    @GetMapping("/download")
    public Result download(String name, String startTime, String endTime, Integer pageNo, Integer pageSize){
        return logService.downloadLog(name, startTime, endTime, pageNo, pageSize);
    }

    /**
     * 导出下载日志
     */
    @GetMapping("/download/export")
    @ResponseBody
    public ResponseEntity downloadLogExport(String name, String startTime, String endTime) {
        // first line
        String firstLine = "序号|姓名|账号|文件名称|下载时间|状态";
        Result result = logService.downloadLog(name, startTime, endTime, 1, 1000);
        List<DownloadRecord> records = (List<DownloadRecord>)result.getData();
        List<String> lineList = new ArrayList<>();
        lineList.add(firstLine);
        for(DownloadRecord loginRecord : records) {
            lineList.add(loginRecord.toString());
        }
        String localFileName = FileUtils.getDownloadFilename(new Date().getTime() +"-download-log.csv");
        File localFile =  new File(localFileName);
        if(!localFile.exists()) {
            localFile.getParentFile().mkdirs();
        }
        try{
            FileWriter fw = new FileWriter(localFileName);
            for(int i = 0; i < lineList.size(); i ++) {
                fw.write(lineList.get(i) + "\n");
            }
            fw.flush();
            fw.close();
        }catch (IOException e) {
            logger.error("查询数据写入本地文件失败" + e.getMessage());
            e.printStackTrace();
        }
        try {
            org.springframework.core.io.Resource file = FileUtils.file2Resource(localFileName);
            if(file == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("数据文件下载失败");
            }
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(file.getFilename(),"UTF-8") + "\"")
                    .body(file);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("文件下载出错");
        }
    }

    /**
     * 查询日志
     */
    @GetMapping("/search")
    public Result search(String name, String startTime, String endTime, Integer pageNo, Integer pageSize){
        return logService.searchLog(name, startTime, endTime, pageNo, pageSize);
    }

    /**
     * 导出下载日志
     */
    @GetMapping("/search/export")
    @ResponseBody
    public ResponseEntity searchLogExport(String name, String startTime, String endTime) {
        // first line
        String firstLine = "序号|姓名|账号|搜索时间|搜索关键字";
        Result result = logService.searchLog(name, startTime, endTime, 1, 1000);
        List<SearchRecord> records = (List<SearchRecord>)result.getData();
        List<String> lineList = new ArrayList<>();
        lineList.add(firstLine);
        for(SearchRecord loginRecord : records) {
            lineList.add(loginRecord.toString());
        }
        String localFileName = FileUtils.getDownloadFilename(new Date().getTime()+"-search-log.csv");
        File localFile =  new File(localFileName);
        if(!localFile.exists()) {
            localFile.getParentFile().mkdirs();
        }
        try{
            FileWriter fw = new FileWriter(localFileName);
            for(int i = 0; i < lineList.size(); i ++) {
                fw.write(lineList.get(i) + "\n");
            }
            fw.flush();
            fw.close();
        }catch (IOException e) {
            logger.error("查询数据写入本地文件失败" + e.getMessage());
            e.printStackTrace();
        }
        try {
            org.springframework.core.io.Resource file = FileUtils.file2Resource(localFileName);
            if(file == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("数据文件下载失败");
            }
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(file.getFilename(),"UTF-8") + "\"")
                    .body(file);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("文件下载出错");
        }
    }
}
