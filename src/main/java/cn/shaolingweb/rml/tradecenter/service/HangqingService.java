package cn.shaolingweb.rml.tradecenter.service;

import cn.shaolingweb.rml.tradecenter.domain.Hq;

import java.util.List;

public interface HangqingService {

    /**
     * 查询单个
     *
     * @param code
     * @return
     */
    public boolean query(List<String> code);

    /**
     * 批量查询
     *
     * @param code
     * @return
     */
    public boolean queryAll(List<String> code);

}
