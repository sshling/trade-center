package cn.shaolingweb.rml.tradecenter.domain.auth;

import lombok.Data;

import java.sql.Timestamp;
@Data
public class Privilege {
    protected Integer id;
    private String name;
    private String code;
    private Integer type;
    private String desc;
    /**
     * 是否有效
     * 0:无效; 1:有效;
     */
    protected Integer valid = 1;
    //修改者
    protected String modifier;
    //修改时间
    protected Timestamp modifiedTime;
    //创建者
    protected String creator;
    //创建时间'
    protected Timestamp createdTime;
}
