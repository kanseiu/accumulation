package com.kanseiu.accumulation.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 表结构
 * @Author: kanseiu
 * @Date: 2022-03-08 10:28
 **/
@Data
public class TableStructure implements Serializable {

    private static final long serialVersionUID = 1L;

    // 表名
    private String tableName;

    // 表注释
    private String tableComments;

    // 字段名
    private String columnName;

    // 字段注释
    private String columnComments;

    // 数据类型
    private String dataType;

    // 是否为空
    private String notNull;

    // 是否为主键
    private String primaryKey;

}
