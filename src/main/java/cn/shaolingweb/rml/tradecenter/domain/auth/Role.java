package cn.shaolingweb.rml.tradecenter.domain.auth;

import lombok.Data;

import java.util.Date;

@Data
public class Role {
    private Integer id;
    private String name;
    private String srcIds;
    private String desId;
    private Integer type;
    private String desc;
    private String creator;
    private Date createdTime;
    private String modifier;
}
