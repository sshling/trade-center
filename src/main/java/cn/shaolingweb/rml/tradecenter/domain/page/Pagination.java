package cn.shaolingweb.rml.tradecenter.domain.page;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
@Data
public class Pagination {

    /**
     * 相关参数
     */
    private Map<String,Object> params = new HashMap<>();

    /**
     * 每页大小
     */
    private int pageSize;

    /**
     * 当前页面start(第一条记录的index)
     */
    private int currentPageStart;

    /**
     * 总数
     */
    private int totalRecord;

}
