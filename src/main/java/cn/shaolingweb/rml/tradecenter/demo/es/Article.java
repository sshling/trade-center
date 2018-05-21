package cn.shaolingweb.rml.tradecenter.demo.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

@Document(indexName = "article",type = "doc",shards = 1,replicas = 0,indexStoreType = "fs")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article implements Serializable {


    private Long id;
    private String title;
    private String abstracts;//摘要
    private String content;

    @Field(format = DateFormat.basic_date_time,
            index = true,
            store = true,type =FieldType.Date)
    private Date postTime;
    private Long clickCount;//点击率
}
