package com.kanseiu.accumulation.controller;

import com.kanseiu.accumulation.api.result.QueryResult;
import com.kanseiu.accumulation.exception.BusinessException;
import com.kanseiu.accumulation.model.CountJavaProjectLines;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: 统计Java工程代码行数
 * 只有/src/main下的.java和.xml文件会被统计（行数统计中会包含注释，但不包括空行）
 * @Author: kanseiu
 * @Date: 2022-03-07 10:57
 **/
@Slf4j
@RestController
@RequestMapping("countJavaProjectLines")
public class CountJavaProjectLinesController {

    private static final String SRC_MAIN_FILE_PATTERN = "(.*)(/src)(/main)?(.{0,10000})(\\.java|\\.xml)$";

    @GetMapping("count")
    public QueryResult<String> count(@RequestParam(value = "filePath") String filePath) {
        return this.countLines(filePath);
    }

    private QueryResult<String> countLines(String filePath) {
        //需要扫描统计的路径
        File file = new File(filePath);
        CountJavaProjectLines countJavaProjectLines = new CountJavaProjectLines(0, 0);
        try {
            scan(file, countJavaProjectLines);
        } catch (IOException e) {
            throw new BusinessException(e.getMessage());
        }
        log.info("扫描完毕！行数为：" + countJavaProjectLines.getLineNums() + "，文件数为：" + countJavaProjectLines.getFileNums());
        return QueryResult.ok("行数为：" + countJavaProjectLines.getLineNums() + "，文件数为：" + countJavaProjectLines.getFileNums());
    }

    public static void scan(File f, CountJavaProjectLines countJavaProjectLines) throws IOException {
        File[] lf = f.listFiles();
        if (Objects.isNull(lf)) return;
        for (File f1 : lf) {
            String path = f1.getAbsolutePath();
            if (f1.isDirectory())
                scan(new File(path), countJavaProjectLines);
            else {
                if (Pattern.matches(SRC_MAIN_FILE_PATTERN, path)) {
                    countJavaProjectLines.setFileNums(countJavaProjectLines.getFileNums() + 1);
                    countLineNums(f1, countJavaProjectLines);
                }
            }
        }
    }

    private static void countLineNums(File f1, CountJavaProjectLines countJavaProjectLines) {
        try (
                FileInputStream fis = new FileInputStream(f1);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
        ) {
            int count = 0;
            String a;
            while ((a = br.readLine()) != null){
                if(StringUtils.hasText(a)) count++;
            }
            countJavaProjectLines.setLineNums(countJavaProjectLines.getLineNums() + count);
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }
}
