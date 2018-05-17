package cn.shaolingweb.rml.tradecenter.domain.auth;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserRole {
    private Integer id;
    private String pin;
    private Integer roleId;
}