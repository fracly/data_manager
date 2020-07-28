package com.bcht.data_manager.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;

import java.util.ArrayList;
import java.util.List;


/**
 * @ClassName ExcelUtil
 * @Description TODO
 * @Author ExcelUtil
 * @Date
 **/
public class ExcelUtils {



    /**
     * @Author liuliang
     * @Description  根据row和第几列。样式 创建单个cell
     * @Date  2020/4/9 0009
     * @Param row 行
     * @Param i  第几个cell，从0开始
     *@Param  value   设置cell的值
     * @Param cellStyle  没有可以为空
     * @return org.apache.poi.hssf.usermodel.HSSFCell
     **/
    public static HSSFCell getCell(HSSFRow row, int i, String value, HSSFCellStyle cellStyle){
        HSSFCell cell = row.createCell(i);
        cell.setCellValue(value);
        if (cellStyle !=null){
            cell.setCellStyle(cellStyle);
        }
        return cell;
    }


    /** 2007版本的excel
     * @Author liuliang
     * @Description  根据row和第几列。样式 创建单个cell
     * @Date  2020/4/9 0009
     * @Param row 行
     * @Param i  第几个cell，从0开始
     *@Param  value   设置cell的值
     * @Param cellStyle  没有可以为空
     * @return org.apache.poi.hssf.usermodel.HSSFCell
     **/
    public static XSSFCell getCell2007(XSSFRow row, int i, String value, XSSFCellStyle cellStyle){
        XSSFCell cell = row.createCell(i);
        cell.setCellValue(value);
        if (cellStyle !=null){
            cell.setCellStyle(cellStyle);
        }
        return cell;
    }

    /**
     * @Author liuliang
     * @Description  一行创建多个cell
     * @Date  2020/4/9 0009
     * @Param [row, map, cellStyle]
     * @return org.apache.poi.hssf.usermodel.HSSFCell
     **/
    public static void getCells(HSSFRow row, String[] cells, HSSFCellStyle cellStyle) {

        for (int i=0;i<  cells.length;i++){
            String cell = cells[i];
            String[] split = cell.split("-");
            int j = Integer.parseInt(split[0].trim());
            String value=split[1].trim();
            HSSFCell ce = row.createCell(j);
            ce.setCellValue(value);
            if (cellStyle != null) {
                ce.setCellStyle(cellStyle);
            }
        }

    }

    public static void getCellsUse(HSSFRow row, String[] cells, HSSFCellStyle cellStyle) {

        for (int i=0;i<  cells.length;i++){
            String cell = cells[i];
            String[] split = cell.split("&");
            int j = Integer.parseInt(split[0].trim());
            String value=split[1].trim();
            HSSFCell ce = row.createCell(j);
            ce.setCellValue(value);
            if (cellStyle != null) {
                ce.setCellStyle(cellStyle);
            }
        }

    }

    /**
     * @Author liuliang
     * @Description  一行创建多个cell，2007版本
     * @Date  2020/4/9 0009
     * @Param [row, map, cellStyle]
     * @return org.apache.poi.hssf.usermodel.HSSFCell
     **/
    public static void getCells2007(XSSFRow row, String[] cells, XSSFCellStyle cellStyle) {

        for (int i=0;i<  cells.length;i++){
            String cell = cells[i];
            String[] split = cell.split("-");
            int j = Integer.parseInt(split[0].trim());
            String value=split[1].trim();
            XSSFCell ce = row.createCell(j);
            ce.setCellValue(value);
            if (cellStyle != null) {
                ce.setCellStyle(cellStyle);
            }
        }

    }


    /**创建row，可以指定style
     * @Author liuliang
     * @Description
     * @Date  2020/4/9 0009
     * @Param [shett, i, cellStyle]
     * @return org.apache.poi.hssf.usermodel.HSSFRow
     **/
    public static HSSFRow getRow(HSSFSheet shett, int i,HSSFCellStyle cellStyle){
        HSSFRow row = shett.createRow(i);
        if (cellStyle != null) {
            row.setRowStyle(cellStyle);
        }
        return row;
    }

    /**创建row，可以指定style，2007版本
     * @Author liuliang
     * @Description
     * @Date  2020/4/9 0009
     * @Param [shett, i, cellStyle]
     * @return org.apache.poi.hssf.usermodel.HSSFRow
     **/
    public static XSSFRow getRow2007(XSSFSheet shett, int i, XSSFCellStyle cellStyle){
        XSSFRow row = shett.createRow(i);
        if (cellStyle != null) {
            row.setRowStyle(cellStyle);
        }
        return row;
    }

    /**
     * @Author liuliang
     * @Description  创建Workbook
     * @return
     */
    public static HSSFWorkbook getWorkbook(){
        HSSFWorkbook wb = new HSSFWorkbook();
        return  wb;
    }


    /**
     * @Author liuliang
     * @Description  创建Workbook,2007版本
     * @return
     */
    public static XSSFWorkbook getWorkbook2007(){
        XSSFWorkbook wb = new XSSFWorkbook();
        return  wb;
    }

    /**
     * @Author liuliang
     * @Description  创建sheet
     * @return
     */
    public static HSSFSheet getSheet(HSSFWorkbook wb,String sheetName){
        HSSFSheet sheet=wb.createSheet(sheetName);//建立sheet对象
        return  sheet;
    }

    /**
     * @Author liuliang
     * @Description  创建sheet,2007版本
     * @return
     */
    public static XSSFSheet getSheet2007(XSSFWorkbook wb,String sheetName){
        XSSFSheet sheet=wb.createSheet(sheetName);//建立sheet对象
        return  sheet;
    }



//    //测试方法
//    public static void main(String[] args){
//        // 设定Excel文件所在路径
//        String excelFileName = "C:\\Users\\Administrator\\Desktop\\测试.xlsx";
//        // 读取Excel文件内容
//        List<Row> rowList = ExcelReaderUtil.readExcel(excelFileName);
//        ArrayList<ExcelDataVO> excelDataVOS = new ArrayList<>();
//        for (   Row r:rowList){
//            ExcelDataVO excelDataVO = convertRowToData(r);
//            if (null == excelDataVO) {
//                   System.out.println("第 " + r.getRowNum() + "行数据不合法，已忽略！");
//                    continue;
//                }
//            excelDataVOS.add(excelDataVO);
//
//        }
//         System.out.println(excelDataVOS.size());
//    }

//    private static ExcelDataVO convertRowToData(Row row) {
//        ExcelDataVO resultData = new ExcelDataVO();
//
//        Cell cell;
//        int cellNum = 0;
//        // 获取姓名
//        cell = row.getCell(cellNum++);
//        String name = ExcelReaderUtil.convertCellValueToString(cell);
//        resultData.setName(name);
//        // 获取身份证明号码
//        cell = row.getCell(cellNum++);
//        String ageStr = ExcelReaderUtil.convertCellValueToString(cell);
//        if (null == ageStr || "".equals(ageStr)) {
//            resultData.setSfzmhm(0);
//        } else {
//            resultData.setSfzmhm(Integer.parseInt(ageStr));
//        }
//        // 获取工作单位
//        cell = row.getCell(cellNum++);
//        String location =ExcelReaderUtil.convertCellValueToString(cell);
//        resultData.setGzdw(location);
//
//        return resultData;
//    }
}

