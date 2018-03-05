package cn.shaolingweb.rml.tradecenter.domain;

public enum  QueryType {
    GEGU("个股"),SHANGZHENG("大盘");
    private String  desc;

    QueryType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
