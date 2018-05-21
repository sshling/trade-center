package cn.shaolingweb.rml.tradecenter.domain.auth;

import lombok.Data;

import java.util.Date;

@Data
public class User {

    private int id;
    private String erp;
    private  String name;
    private String telephone;
    private String email;
    private Date createTime;
    private Date modifyTime;

}
