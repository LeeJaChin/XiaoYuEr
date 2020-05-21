###log4j2配置kafka输出，增加sasl鉴权
```
<Kafka name="kafka" topic="test" ignoreExceptions="false" syncSend="true">
            <Property name="bootstrap.servers">localhost:9092</Property>
            <Property name="max.block.ms">2000</Property>
            <Property name="sasl.mechanism">PLAIN</Property>
            <Property name="security.protocol">SASL_PLAINTEXT</Property>
            <Property name="sasl.jaas.config">org.apache.kafka.common.security.plain.PlainLoginModule required username="admin" password="admin-secret";</Property>
        </Kafka>
```