package com.kanseiu.accumulation.controller;

import com.kanseiu.accumulation.api.result.QueryResult;
import com.kanseiu.accumulation.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @Description: 统计Java工程代码行数
 * @Author: kanseiu
 * @Date: 2022-03-07 10:57
 **/
@Slf4j
@RestController
@RequestMapping("countJavaProjectLines")
public class CountJavaProjectLinesController {

    private final static String SRC_DIR_PATTERN = ".*/src";

    private final static String SRC_DIR_PATTERN1 = ".*/src/.*";

    @GetMapping("count")
    public QueryResult<?> count(@RequestParam(value = "filePath") String filePath){
        return this.countLines(filePath);
    }

    private QueryResult<?> countLines(String filePath){
        //需要扫描统计的路径
        File file = new File(filePath);
        String str;
        try {
            str = scan(file,0,0);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
        log.info("扫描完毕");
        String[] str2 = str.split(",");
        //将返回出的字符串解析为数字
        int lineNum = Integer.parseInt(str2[0]);
        int fileNum = Integer.parseInt(str2[1]);
        return QueryResult.ok("行数为：" + lineNum + "，文件数为：" + fileNum);
    }

    public static String scan(File f, int LineNumber, int FileNumber) throws IOException {
        int lineNum = LineNumber;
        int fileNum = FileNumber;
        // 将传入的File对象变成File数组
        File[] lf = f.listFiles();
        // 如果为空则结束这次方法。避免空指针异常
        if (Objects.isNull(lf))
            return null;
        // 循环遍历lf中的每个File对象
        for (File f1 : lf) {
            // 如果当前遍历到的这个File对象是文件夹
            if (f1.isDirectory()) {
                // 得到当前文件夹的路径
                String path = f1.getAbsolutePath();
                if(Pattern.matches(SRC_DIR_PATTERN, path) || Pattern.matches(SRC_DIR_PATTERN1, path)) {
                    // 重新调用当前方法，并传入刚刚遍历到的文件夹对象，行数和文件数，并用一个String接收返回的字符串
                    String test = scan(new File(path), lineNum, fileNum);

                    String[] str2 = test.split(",");
                    //将拿到的返回出的字符串解析为行数和文件数
                    lineNum = Integer.parseInt(str2[0]);
                    fileNum = Integer.parseInt(str2[1]);
                }
            } else {
                //判断是不是java文件
                if(f1.getName().endsWith(".java") || f1.getName().endsWith(".xml")){
                    fileNum++;
                    //创建当前文件的对象
                    File file = new File(f1.getAbsolutePath());
                    //创建字符流
                    FileReader fr = new FileReader(file);
                    int i;
                    while((i = fr.read()) != -1) {
                        Character c = (char)i;
                        //将读出的字符转换为字符串
                        String temp = c.toString();
                        //判断字符串中有没有换行
                        if(temp.contains("\n"))
                            lineNum++;
                    }
                    //关闭字符流
                    fr.close();
                }
            }
        }
        System.out.println("file = " + f.getAbsolutePath() + ", lineNum = " + lineNum + ", fileNum = " + fileNum);
        //将行数和文件数返回
        return lineNum + "," + fileNum;
    }

}
