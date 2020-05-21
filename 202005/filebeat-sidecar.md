#filebeat收集容器日志
##收集方式
- daemonSet方式
- sidecar方式
##sidecar方式配置文件示例
```yaml
# app服务
---
apiVersion: v1
kind: Service
metadata:
  name: test-app
  labels:
    app: test-app
spec:
  selector:
    app: test-app
  ports:
  - protocol: TCP
    port: 8080
    targetPort: 8080
    name: test-port
#定义日志收集相关配置的一个configmap
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: test-filebeat-config
  labels:
    k8s-app: filebeat
data:
  filebeat.yml: |-
    filebeat.prospectors:
    - type: log
      paths:
        - /logdata/*.log
      tail_files: true
      fields:
        pod_name: '${pod_name}'
        POD_IP: '${POD_IP}'
    setup.template.name: "app-logs"
    setup.template.pattern: "app-logs-*"
    output.elasticsearch: # 日志输出到ES
      hosts: ["192.168.1.xx:9200","192.168.1.xxx:9200"]
      index: "app-logs-%{+yyyy.MM}"
# deployment, 也可通过daemonset方式
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: test-app
spec:
  replicas: 1
  minReadySeconds: 15     #滚动升级15s后标志pod准备就绪
  strategy:
    rollingUpdate:        #replicas为2, 升级过程中pod个数在1-3个之间
      maxSurge: 1         #滚动升级时会先启动1个pod
      maxUnavailable: 1   #滚动升级时允许pod处于Unavailable的最大个数
  selector:
    matchLabels:
      app: test-app
  template:
    metadata:
      labels:
        app: test-app
    spec:
      terminationGracePeriodSeconds: 30 #30秒内优雅关闭程序
      containers:
      - image: hub.exmaple.com/publib/filebeat:6.1.3     #提前下载下来到私有镜像库的镜像(官方的可能会被墙)
        name: filebeat
        args: [
          "-c", "/opt/filebeat/filebeat.yml",
          "-e",
        ]
        env:
        - name: POD_IP
          valueFrom:
            fieldRef:
              apiVersion: v1
              fieldPath: status.podIP
        - name: pod_name
          valueFrom:
            fieldRef:
              apiVersion: v1
              fieldPath: metadata.name
        securityContext:
          runAsUser: 0
        resources:
          limits:
            memory: 200Mi
          requests:
            cpu: 200m
            memory: 200Mi
        volumeMounts:
        - name: config               #将configmap的内容放到容器本地目录
          mountPath: /opt/filebeat/
        - name: data
          mountPath: /usr/share/filebeat/data
        - name: logdata       #同一个pod内的两个应用共享目录logdata, 一个写一个读
          mountPath: /logdata
      - name: test-app
        image: hub.example.com/service/test-service:latest  #提供具体服务的app镜像
        ports:
        - containerPort: 8080
        volumeMounts:
        - name: logdata       #指定挂在目录到logdata
          mountPath: /usr/local/tomcat/logs
      volumes:
      - name: data
        emptyDir: {}
      - name: logdata         #定义logdata为EmptyDir类型挂载目录
        emptyDir: {}
      - name: config
        configMap:
          name: test-filebeat-config  #使用前面定义的configmap
          items:
          - key: filebeat.yml
            path: filebeat.yml

```