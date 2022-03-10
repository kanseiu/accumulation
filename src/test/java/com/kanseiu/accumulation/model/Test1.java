package com.kanseiu.accumulation.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description:
 * @Author: kanseiu
 * @Date: 2022-03-03 18:31
 **/
@Data
@TableName("test_1")
public class Test1 implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

}
