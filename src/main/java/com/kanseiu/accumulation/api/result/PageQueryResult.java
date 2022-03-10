package com.kanseiu.accumulation.api.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kanseiu.accumulation.util.StringUtil;
import lombok.Data;

import java.util.List;

@Data
public class PageQueryResult<T> extends QueryResult<T> {
    /** 页面容量 */
    private long pageSize;
    /** 当前页面序号，从1开始 */
    private long pageIndex;
    /** 结果总记录数 */
    private long totalRecordCount;
    /** 总分页数 */
    private long pageCount;

    private static <TData> PageQueryResult<TData> CreatePageQueryResult(boolean succeed, TData data, int code, String message){
        PageQueryResult<TData> result = new PageQueryResult<>();
        result.setSuccess(succeed);
        result.setData(data);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <TData> PageQueryResult<TData> fail(TData data, String msg){
        return CreatePageQueryResult(false, data, ResultCodes.FAIL, msg);
    }

    public static <TData> PageQueryResult<TData> ok(TData data, String msg){
        return CreatePageQueryResult(true, data, ResultCodes.SUCCESS, msg);
    }

    public static <TData> PageQueryResult<List<TData>> fromPageResult(IPage<TData> pageResult){
        PageQueryResult<List<TData>> result =  ok(pageResult.getRecords(), StringUtil.Empty);
        result.setPageIndex(pageResult.getCurrent());
        result.setPageSize(pageResult.getSize());
        result.setTotalRecordCount(pageResult.getTotal());
        result.setPageCount(pageResult.getPages());
        result.setData(pageResult.getRecords());
        return result;
    }

}
