package cn.shaolingweb.rml.tradecenter.util;

import cn.shaolingweb.rml.tradecenter.domain.AlarmConf;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.ArrayList;
import java.util.List;

/*
fastjson
    Student stu2 = JSON.parseObject(jsonText,Student.class);

  Student[] stu2 = JSON.parseObject(jsonText,new TypeReference<Student[]>(){});

  ArrayList<Student> list2 = JSON.parseObject(jsonText, new TypeReference<ArrayList<Student>>(){});


 */
public class JsonUtil {
    public void toObj(String jsonStr){

    }

    public static List<AlarmConf> toList(String jsonStr){
        return JSON.parseObject(jsonStr, new TypeReference<ArrayList<AlarmConf>>(){});
    }
}
