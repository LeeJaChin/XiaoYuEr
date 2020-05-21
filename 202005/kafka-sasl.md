#kafka集群增加多认证机制
##每个broker修改server.properties
```
listeners=SASL_PLAINTEXT://:9092
security.inter.broker.protocol=SASL_PLAINTEXT
sasl.mechanism.inter.broker.protocol=SCRAM-SHA-256
sasl.enabled.mechanisms=SCRAM-SHA-256,PLAIN
authorizer.class.name=kafka.security.auth.SimpleAclAuthorizer
allow.everyone.if.no.acl.found=true
super.users=User:admin
```
##在kafka config目录创建kafka_server_jaas.conf
```
KafkaServer {
        org.apache.kafka.common.security.scram.ScramLoginModule required
        username="admin"
        password="admin-secret";
      
        org.apache.kafka.common.security.plain.PlainLoginModule required
        username="admin"
        password="admin-secret"
        user_admin="admin-secret"
        user_alice="alice-secret";
    };
```
##在kafka config目录创建kafka_client_jaas.conf
```
KafkaClient { 
    org.apache.kafka.common.security.plain.PlainLoginModule required 
    username="admin" password="admin-secret"; 
};
```
##在kafka bin目录修改kafka-server-start.sh,最后一行
```
exec $base_dir/kafka-run-class.sh $EXTRA_ARGS -Djava.security.auth.login.config=/app/kafka/config/kafka_server_jaas.conf kafka.Kafka "$@"
```
##修改consumer.properties和producer.properties
```
security.protocol=SASL_PLAINTEXT
#sasl.mechanism=SCRAM-SHA-256
sasl.mechanism=PLAIN
```
##在kafka bin目录修改kafka-console-consumer.sh和kafka-console-producer.sh,最后一行上面增加
```
export KAFKA_OPTS="-Djava.security.auth.login.config=/app/kafka/config/kafka_client_jaas.conf"
exec $(dirname $0)/kafka-run-class.sh kafka.tools.ConsoleProducer "$@"
```
##使用kafka-configs创建用户
```
bin/kafka-configs.sh --zookeeper localhost:2181 --alter --add-config 'SCRAM-SHA-256=[password=admin-secret],SCRAM-SHA-512=[password=admin-secret]' --entity-type users --entity-name admin
```
##使用kafka-acls.sh赋值权限
```
bin/kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --add --allow-principal User:admin --operation ALL --group=* --topic=*
```
##console生产和消费
```
bin/kafka-console-producer.sh --broker-list 10.200.201.24:9092 --topic test --producer.config config/producer.properties
bin/kafka-console-consumer.sh --bootstrap-server 10.200.201.24:9092 --topic test --from-beginning --consumer.config config/consumer.properties
```
##附logstash消费配置
```
kafka{
    codec => "json"
    auto_offset_reset => "earliest"
    bootstrap_servers => "10.200.201.24:9092"
    group_id => "test"
    topics => ["test"]
    client_id => "logstash-test"
    sasl_mechanism => "SCRAM-SHA-256"
    security_protocol => "SASL_PLAINTEXT"
    sasl_jaas_config => "org.apache.kafka.common.security.scram.ScramLoginModule required username='admin'  password='admin-secret';"

}
```