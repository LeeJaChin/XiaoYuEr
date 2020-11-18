#spring boot 配置https
```yaml
server:
  ssl:
    key-store: /app/my.jks
    key-store-password: 1q2w3e4r
    key-store-type: JKS
```
- 如果使用tomcat当做servlet的容器，当前需要将jks格式的证书不能使用classpath：my.jks，需要一个文件目录
- 通过xxx.csr证书申请文件，申请的pem证书和私钥key文件，导入jks的密码库，转换生成jks文件。