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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if(!(obj instanceof Privilege)) {
            return false;
        }
        Privilege other = (Privilege)obj;
        if (!code.equals(other.code)) {
            return false;
        }
        return true;
    }
}
