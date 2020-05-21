#基于snakeYaml生成filebeat配置文件
##FilebeatConfig.java
```java
package com.utils;

import lombok.Data;

import java.util.List;

/**
 * Created by lijiaqi by 2020/4/14.
 *
 * @author lijiaqi
 */
@Data
public class FilebeatConfig {

    private FilebeatInput filebeat;

    private FilebeatOutput output;



}
@Data
class FilebeatInput{
    private List<FilebeatType> inputs;
}

@Data
class FilebeatType{
    private boolean enabled;
    private String type;
    private List<String> paths;
    private FilebeatFields fields;
    private boolean fields_under_root;
}

@Data
class FilebeatFields{
    private String type;
}

@Data
class FilebeatOutput{
    private FilebeatOutKafka kafka;
}

@Data
class FilebeatOutKafka{
    private String[] hosts;
    private String topic;
    private Integer required_acks;
    private String compression;
    private String max_message_bytes;

}

```
##FilebeatConfigurationGenerator.class
```java
package com.utils;

import com.config.ShellConfig;
import com.common.LogTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lijiaqi by 2020/4/14.
 *
 *  auto create filebeat configuration
 * @author lijiaqi
 */
public class FilebeatConfigurationGenerator {
    private static final Logger log = LoggerFactory.getLogger(FilebeatConfigurationGenerator.class);

    private static ShellConfig shellConfig = SpringUtils.getBean(ShellConfig.class);


    /**
     *
     * @param topic
     * @param logPaths
     * @param typeEnum
     * @return
     */
    public static String createFilebeatConfig(String ip,String topic, List<String> logPaths, LogTypeEnum typeEnum){
        try {
            String filebeatConfigName =  "filebeat-" + ip + "-" + topic + "-" + DateUtils.dateToStr(new Date(),"yyyyMMddHHmmssSSS")+".yml";
            FilebeatConfig filebeatConfig = new FilebeatConfig();
            FilebeatInput input = new FilebeatInput();
            List<FilebeatType> types = new ArrayList<>();
            FilebeatFields filebeatFields = new FilebeatFields();
            filebeatFields.setType(topic);
            FilebeatType filebeatType = new FilebeatType();
            filebeatType.setType(typeEnum.getValue());
            filebeatType.setPaths(logPaths);
            filebeatType.setEnabled(true);
            filebeatType.setFields(filebeatFields);
            filebeatType.setFields_under_root(true);
            types.add(filebeatType);
            input.setInputs(types);
            filebeatConfig.setFilebeat(input);
            FilebeatOutput filebeatOutput = new FilebeatOutput();
            FilebeatOutKafka filebeatOutKafka = new FilebeatOutKafka();
            filebeatOutKafka.setHosts(new String[]{shellConfig.getKafkaHost()});
            filebeatOutKafka.setTopic("%{[type]}");
            filebeatOutKafka.setCompression("gzip");
            filebeatOutKafka.setRequired_acks(1);
            filebeatOutKafka.setMax_message_bytes("1000000");
            filebeatOutput.setKafka(filebeatOutKafka);
            filebeatConfig.setOutput(filebeatOutput);
            Yaml yaml =new Yaml();
            FileWriter fileWriter = new FileWriter(new File( shellConfig.getLogstashFilePath() + filebeatConfigName));
            yaml.dump(filebeatConfig, fileWriter);
            return filebeatConfigName;
        } catch (IOException e) {
            log.error("createFilebeatConfig IOException error",e);
        } catch (Exception e){
            log.error("createFilebeatConfig Exception error",e);
        }
        return null;
    }

}

```