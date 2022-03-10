package com.kanseiu.accumulation.service.impl;

import com.kanseiu.accumulation.api.result.ActionResult;
import com.kanseiu.accumulation.service.ExcelToPictureService;
import com.spire.xls.Workbook;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @Description:
 * @Author: kanseiu
 * @Date: 2022-03-01 16:16
 **/
@Service
@Slf4j
public class ExcelToPictureServiceImpl implements ExcelToPictureService {

    private final static int DPI_X = 300;

    private final static int DPI_Y = 300;

    private final static int SLOT = 3;

    private final static int FIRST_SHEET_INDEX = 0;

    @Override
    public ActionResult handler(MultipartFile file) throws Exception{
        String fileContentType = file.getContentType();
        switch (fileContentType) {
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                this.excelToPicture(file, XSSFWorkbook.class);
                break;
            case "application/vnd.ms-excel":
                this.excelToPicture(file, HSSFWorkbook.class);
                break;
            default:
                break;
        }
        return ActionResult.ok();
    }

    private void excelToPicture(MultipartFile file, Class<? extends org.apache.poi.ss.usermodel.Workbook> clazz) throws Exception {
        org.apache.poi.ss.usermodel.Workbook wb = clazz.getDeclaredConstructor(InputStream.class).newInstance(file.getInputStream());
        int numOfSheets = wb.getNumberOfSheets(), count = 0, max = numOfSheets, handleTimes = 0;
        log.info("正在将[{}]转换为图片，sheet页总数为{}...", file.getOriginalFilename(), numOfSheets);
        while (count != max){
            for (int j = 0; j < numOfSheets; j++) {
                if (j >= SLOT) wb.removeSheetAt(SLOT);
                else count++;
            }
            this.outputStreamToSpireWorkbook(wb, handleTimes);
            if (count == max) break;
            wb = clazz.getDeclaredConstructor(InputStream.class).newInstance(file.getInputStream());
            for (int k = 0; k < count; k++)
                wb.removeSheetAt(FIRST_SHEET_INDEX);
            numOfSheets = wb.getNumberOfSheets();
            handleTimes++;
        }
        log.info("[{}]的全部sheet页已转换完毕。", file.getOriginalFilename());
        wb.close();
    }

    private void outputStreamToSpireWorkbook(org.apache.poi.ss.usermodel.Workbook wb, int handleTimes) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        wb.write(bos);
        InputStream in = new ByteArrayInputStream(bos.toByteArray());
        Workbook wb_spire = new Workbook();
        wb_spire.loadFromStream(in);
        this.processExcelFileToPic(wb_spire, handleTimes);
        in.close();
        bos.close();
    }

    private void processExcelFileToPic(Workbook wb, int handleTimes) throws IOException {
        int sheetCount = wb.getWorksheets().getCount();
        for (int i = 0; i < sheetCount; i++) {
            log.info("当前正在处理第{}个sheet页, sheet = [{}]", (handleTimes * SLOT + i + 1), wb.getWorksheets().get(i).getName());
            BufferedImage image = wb.saveAsImage(i, DPI_X, DPI_Y);
            File outputFile = new File(wb.getWorksheets().get(i).getName() + ".png");
            ImageIO.write(image, "png", outputFile);
        }
    }
}
