package com.kanseiu.accumulation.service;

import com.kanseiu.accumulation.api.result.ActionResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description:
 * @Author: kanseiu
 * @Date: 2022-03-01 16:16
 **/
public interface ExcelToPictureService {

    ActionResult handler(MultipartFile file) throws Exception;

}
