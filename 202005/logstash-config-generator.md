#生成logstash配置文件
##LogstashConfigurationGenerator.class
```java
package com.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ShellConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lijiaqi by 2020/4/14.
 *
 *  auto create logstash config file
 * @author lijiaqi
 */
public class LogstashConfigurationGenerator {

    private static final Logger log = LoggerFactory.getLogger(LogstashConfigurationGenerator.class);

    private static ShellConfig shellConfig = SpringUtils.getBean(ShellConfig.class);

    private static BufferedWriter writer;

    private static void generateInputKafka(Map<String, Object> params) {
        try {
            writer.write("input {");
            writer.newLine();
            writer.write("\tkafka {");
            writer.newLine();
            params.forEach((k,v) ->{
                try {
                    writer.write("\t\t"+k +" => "+v);
                    writer.newLine();
                } catch (IOException e) {
                   log.error(" params cycle error",e);
                }
            });
            writer.write("\t}");
            writer.newLine();
            writer.write("}");
            writer.newLine();
        } catch (IOException e) {
            log.error(" generateInputKafka error",e);
        }


    }
    private static void generateFilter() {
        try {
            writer.write("filter {}");
            writer.newLine();
        } catch (IOException e) {
            log.error("generateFilter error",e);
        }

    }

    private static void generateOutputElasticsearch(Map<String, Map<String,Object>> params){
        try {
            writer.write("output {");
            writer.newLine();
            int index = 0;
            for (Map.Entry<String, Map<String, Object>> entry : params.entrySet()) {
                String k = entry.getKey();
                Map<String, Object> v = entry.getValue();
                if(index ==0) {
                    writer.write("\tif [log][file][path] == \"" + k + "\" {");
                }else{
                    writer.write("\telse if [log][file][path] == \"" + k + "\" {");
                }
                writer.newLine();
                writer.write("\t\telasticsearch {");
                writer.newLine();
                for (Map.Entry<String, Object> e : v.entrySet()) {
                    writer.write("\t\t\t"+e.getKey() +" => "+e.getValue());
                    writer.newLine();
                }
                writer.write("\t\t}");
                writer.newLine();
                writer.write("\t}");
                index++;
            }
            writer.newLine();
            writer.write("}");
            writer.newLine();
        } catch (IOException e) {
            log.error(" generateOutputElasticsearch error",e);
        }
    }

    private static void generateOutputElasticsearchActiveLogstash(Map<String,Object> param){
        try {
            writer.write("output {");
            writer.newLine();
            writer.write("\t\telasticsearch {");
            writer.newLine();
            for (Map.Entry<String, Object> e : param.entrySet()) {
                writer.write("\t\t\t"+e.getKey() +" => "+e.getValue());
                writer.newLine();
            }
            writer.write("\t\t}");
            writer.newLine();
            writer.write("}");
            writer.newLine();
        } catch (IOException e) {
            log.error(" generateOutputElasticsearch error",e);
        }
    }

    public static String createLogstashFile(JSONObject jsonObject){
        try {
            String configFileName =  "logstash-" + jsonObject.getString("ip") + "-" + jsonObject.getString("topic") + "-" + DateUtils.dateToStr(new Date(),"yyyyMMddHHmmssSSS")+".conf";
            File inputBeatsFile = new File(shellConfig.getLogstashFilePath()+configFileName);
            writer = new BufferedWriter(new FileWriter(inputBeatsFile));
            generateInputKafka(buildKafka(jsonObject.getString("topic")));
            generateFilter();
            generateOutputElasticsearch(buildElasticsearch(jsonObject));
            writer.close();
            return configFileName;
        } catch (IOException e) {
            log.error("createLogstashFile IOException error",e);
        } catch (Exception e) {
            log.error("createLogstashFile Exception error",e);
        }
        return null;
    }

    public static String createLogstashFileActiveLogstash(JSONObject jsonObject){
        try {
            String configFileName =  "logstash-active-" + jsonObject.getString("topic") + "-" + DateUtils.dateToStr(new Date(),"yyyyMMddHHmmssSSS")+".conf";
            File inputBeatsFile = new File(shellConfig.getLogstashFilePath()+configFileName);
            writer = new BufferedWriter(new FileWriter(inputBeatsFile));
            generateInputKafka(buildKafka(jsonObject.getString("topic")));
            generateFilter();
            generateOutputElasticsearchActiveLogstash(buildElasticsearchActiveLogstash(jsonObject));
            writer.close();
            return configFileName;
        } catch (IOException e) {
            log.error("createLogstashFile IOException error",e);
        } catch (Exception e) {
            log.error("createLogstashFile Exception error",e);
        }
        return null;
    }

    private static Map<String,Object> buildKafka(String topic){
        Map<String,Object> param = new HashMap<>();
        param.put("codec","\"json\"");
        param.put("auto_offset_reset","\"earliest\"");
        param.put("bootstrap_servers","\""+shellConfig.getKafkaHost()+"\"");
        param.put("group_id","\""+topic+"\"");
        param.put("topics","[\""+topic+"\"]");
        param.put("client_id","\"logstash-"+topic+"\"");
        return param;
    }

    private static Map<String,Map<String,Object>> buildElasticsearch(JSONObject jsonObject){
        Map<String,Map<String,Object>> res = new HashMap<>();
        JSONArray array = jsonObject.getJSONArray("apiConfigLogIndexDtoList");
        for (int i = 0 ; i < array.size() ; i++){
            JSONObject temp = array.getJSONObject(i);
            Map<String,Object> tmp = new HashMap<>();
            tmp.put("hosts","[\""+shellConfig.getEsHost()+"\"]");
            tmp.put("index","\""+temp.getString("indexName")+"\"");
            tmp.put("user",shellConfig.getEsWriteUsername());
            tmp.put("password",shellConfig.getEsWritePassword());
            tmp.put("custom_headers","{\"X-Found-Cluster\" => \""+jsonObject.getString("esResourceId")+"\"}");
            res.put(temp.getString("logPath"),tmp);
        }
        return res;
    }

    private static Map<String,Object> buildElasticsearchActiveLogstash(JSONObject jsonObject){
        Map<String,Object> param = new HashMap<>();
        param.put("hosts","[\""+shellConfig.getEsHost()+"\"]");
        param.put("index","\""+jsonObject.getString("indexName")+"\"");
        param.put("user",shellConfig.getEsWriteUsername());
        param.put("password",shellConfig.getEsWritePassword());
        param.put("custom_headers","{\"X-Found-Cluster\" => \""+jsonObject.getString("esResourceId")+"\"}");
        return param;
    }

}

```