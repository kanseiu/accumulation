package com.kanseiu.accumulation.model;

import lombok.Data;

/**
 * @Description:
 * @Author: kanseiu
 * @Date: 2022-03-11 16:29
 **/
@Data
public class CountJavaProjectLines {

    public CountJavaProjectLines(){}

    public CountJavaProjectLines(Integer lineNums, Integer fileNums){
        this.lineNums = lineNums;
        this.fileNums = fileNums;
    }

    private Integer lineNums;

    private Integer fileNums;
}
