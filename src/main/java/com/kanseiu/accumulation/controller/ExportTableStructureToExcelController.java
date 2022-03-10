package com.kanseiu.accumulation.controller;

import com.kanseiu.accumulation.api.result.QueryResult;
import com.kanseiu.accumulation.exception.BusinessException;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 导出数据库表结构到Excel
 * @Author: kanseiu
 * @Date: 2022-03-07 17:48
 **/

@Slf4j
@RestController
@RequestMapping("exportTableStructureToExcel")
public class ExportTableStructureToExcelController {

    private static Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();

    private final static int STANDARD_COL_WIDTH = 12 * 256;

    private final static int MAX_COL_WIDTH = 50 * 256;

    /**
     * 导出
     *
     * @param driver   数据库driver
     * @param url      数据库IP
     * @param username 用户名
     * @param pwd      密码
     * @return
     */
    @PostMapping("export")
    public QueryResult<?> upload(@RequestParam("driver") String driver,
                                 @RequestParam("url") String url,
                                 @RequestParam("username") String username,
                                 @RequestParam("pwd") String pwd,
                                 @RequestParam("sqlFile") MultipartFile sqlFile,
                                 @RequestParam("otherCol") String otherCol,
                                 @RequestParam("tableNameCol") String tableNameCol,
                                 @RequestParam("tableCommentsCol") String tableCommentsCol,
                                 HttpServletResponse response) throws Exception {

        String sql = this.readFile(sqlFile);

        String[] col = otherCol.split(",");

        //创建数据源连接池
        DataSource dataSource = this.getDataSource(driver, url, username, pwd);

        //创建jdbc模板
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);

        Map<String, List<Map<String, String>>> all = new HashMap<>();
        jdbcTemplate.query(sql, resultSet -> {
            Map<String, String> map = new HashMap<>();
            for (String c : col)
                map.put(c, resultSet.getString(c));
            map.put(tableCommentsCol, resultSet.getString(tableCommentsCol));
            map.put(tableNameCol, resultSet.getString(tableNameCol));

            if(all.containsKey(resultSet.getString(tableNameCol))){
                all.get(resultSet.getString(tableNameCol)).add(map);
            } else {
                List<Map<String, String>> list = new ArrayList<>();
                all.put(resultSet.getString(tableNameCol), list);
                list.add(map);
            }
        });
        this.generateExcel(all, col, tableNameCol, response);
        return QueryResult.ok("导出Excel成功！");
    }

    private String readFile(MultipartFile file) throws Exception {
        try {
            ByteArrayInputStream certBis = new ByteArrayInputStream(file.getBytes());
            InputStreamReader input = new InputStreamReader(certBis);
            BufferedReader bf = new BufferedReader(input);
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = bf.readLine()) != null) {
                sb.append(line).append("\n");
            }
            certBis.close();
            input.close();
            bf.close();
            return sb.toString();
        } catch (Exception e){
            throw new BusinessException("sql文件读取失败！请检查并修改后重试！");
        }
    }

    private void generateExcel(Map<String, List<Map<String, String>>> map, String[] col, String tableCommentsCol, HttpServletResponse response) throws Exception {
        if (map.isEmpty())
            throw new BusinessException("查询不到任何数据！请检查sql文件后重试");

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        XSSFSheet xssfSheet = xssfWorkbook.createSheet();
        int rowNum1 = 0;

        // 垂直居中 + 边框 + 字体
        XSSFCellStyle cellBorderBasicStyle = this.cellBoardThin(xssfWorkbook.createCellStyle());
        XSSFCellStyle verticalAndHorizontalAlignmentStyle = this.verticalAndHorizontalAlignment(cellBorderBasicStyle);
        XSSFCellStyle style1 = this.cellContentType(xssfWorkbook.createFont(), verticalAndHorizontalAlignmentStyle);

        // 边框 + 字体
        XSSFCellStyle cellBorderBasicStyle1 = this.cellBoardThin(xssfWorkbook.createCellStyle());
        XSSFCellStyle style2 = this.cellContentType(xssfWorkbook.createFont(), cellBorderBasicStyle1);
        style2.setVerticalAlignment(VerticalAlignment.CENTER);

        for (Map.Entry<String, List<Map<String, String>>> stringListEntry : map.entrySet()) {
            String tableName = stringListEntry.getKey();
            String tableComments = stringListEntry.getValue().get(0).get(tableCommentsCol);

            // 设置表名
            XSSFCell xssfCell;
            String cellValue = StringUtils.hasText(tableComments) ? tableName + "(" + tableComments + ")" : tableName;
            XSSFRow xssfRow = xssfSheet.createRow(rowNum1);
            for (int i = 0; i < col.length; i++) {
                xssfCell = xssfRow.createCell(i);
                xssfCell.setCellValue(cellValue);
                xssfCell.setCellStyle(style1);
            }
            this.setMerged(xssfSheet, rowNum1, rowNum1, 0, col.length - 1);

            // 设置字段名
            xssfRow = xssfSheet.createRow(++rowNum1);
            for (int i = 0; i < col.length; i++) {
                xssfCell = xssfRow.createCell(i);
                xssfCell.setCellValue(col[i]);
                xssfCell.setCellStyle(style2);
            }

            // 设置值
            for (int i = 0; i < stringListEntry.getValue().size(); i++) {
                Map<String, String> tableStructure = stringListEntry.getValue().get(i);
                xssfRow = xssfSheet.createRow(++rowNum1);
                for (int j = 0; j < col.length; j++) {
                    xssfCell = xssfRow.createCell(j);
                    xssfCell.setCellValue(tableStructure.get(col[j]));
                    xssfCell.setCellStyle(style2);
                }
            }
            // 加入空行
            xssfSheet.createRow(++rowNum1);
            this.setMerged(xssfSheet, rowNum1, rowNum1, 0, col.length - 1);
            rowNum1++;
        }

        // 设置单元格宽度
        for(int i = 0; i < col.length; i++){
            xssfSheet.autoSizeColumn(i);
            int width = Math.max(STANDARD_COL_WIDTH, Math.min(MAX_COL_WIDTH, xssfSheet.getColumnWidth(i)));
            xssfSheet.setColumnWidth(i, width);
        }

        //用输出流写到excel
        try {
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            response.setHeader("charset", "utf-8");
            String excelName = "TableStructure.xlsx";
            response.setHeader("Content-Disposition", "attachment;filename=\"" + URLEncoder.encode(excelName, StandardCharsets.UTF_8) + "\"");

            OutputStream outputStream = response.getOutputStream();
            xssfWorkbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 垂直居中样式
    private XSSFCellStyle verticalAndHorizontalAlignment(XSSFCellStyle xssfCellStyle) {
        xssfCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        xssfCellStyle.setAlignment(HorizontalAlignment.CENTER);
        return xssfCellStyle;
    }

    // 字体
    private XSSFCellStyle cellContentType(XSSFFont font, XSSFCellStyle cellStyle){
        font.setFontHeightInPoints((short) 12);// 字号
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        return cellStyle;
    }

    // 设置单元格边框
    private XSSFCellStyle cellBoardThin(XSSFCellStyle cellStyle) {
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        return cellStyle;
    }

    // 单元格合并居中
    private void setMerged(XSSFSheet xssfSheet, int rowBegin, int rowEnd, int colBegin, int colEnd) {
        CellRangeAddress cra = new CellRangeAddress(rowBegin, rowEnd, colBegin, colEnd);
        xssfSheet.addMergedRegion(cra);
    }

    private DataSource getDataSource(String driver, String url, String username, String pwd) throws Exception {
        String key = driver.concat(";").concat(url).concat(";").concat(username).concat(";").concat(pwd);
        if (dataSourceMap.containsKey(key)) {
            return dataSourceMap.get(key);
        } else {
            //创建数据源连接池
            HikariDataSource dataSource = DataSourceBuilder
                    .create()
                    .type(HikariDataSource.class)
                    .url(url)
                    .driverClassName(driver)
                    .username(username)
                    .password(pwd)
                    .build();
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                connection.beginRequest();
                dataSourceMap.put(key, dataSource);
                return dataSource;
            } catch (Exception e) {
                if (!dataSource.isClosed()) dataSource.close();
                throw new Exception("测试连接失败！" + e.getMessage());
            } finally {
                if (!Objects.isNull(connection) && !connection.isClosed()) connection.close();
            }
        }
    }
}