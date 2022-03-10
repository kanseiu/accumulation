package com.kanseiu.accumulation.controller;

import com.kanseiu.accumulation.api.result.ActionResult;
import com.kanseiu.accumulation.api.result.QueryResult;
import com.kanseiu.accumulation.service.ExcelToPictureService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Description: Excel转图片
 * @Author: kanseiu
 * @Date: 2022-03-01 16:10
 **/
@RestController
@RequestMapping("excelToPicture")
public class ExcelToPictureController {

    @Resource
    private ExcelToPictureService excelToPictureService;

    /**
     * 上传excel文件
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("uploadExcel")
    public QueryResult<?> upload(@RequestParam("file") MultipartFile file) throws Exception{
        ActionResult uploadResult = excelToPictureService.handler(file);
        return QueryResult.ok(uploadResult.getMessage());
    }

}
