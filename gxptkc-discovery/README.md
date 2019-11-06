灰度发布，使用了Nepxion Discovery 灰度框架进行集成。
====
Nepxion Discovery 具体介绍请查看：https://github.com/Nepxion/Discovery

### 使用说明:
使用nacos配置中心进行灰度开启/关闭灰度发布\
    登陆进入nacos配置中心，找到Data Id为discovery_conf，Group为discovery_group配置项目,(discovery_conf和discovery_group 该两个值固定不变),具体内容如：
```
{
    "service":"server-sound:1.1",
    "clientVersion": "",
    "remoteIp":"127.0.0.1,192.168.",
    "isOpen":true
}
```
灰度发布配置说明：\
    1、service：指定某个服务名进行灰度测试，多个服务使用英文，隔开,如：server-sound:1.1,server-opreation:1.0\
    2、clientVersion：可针对前端版本号进行灰度测试，多个版本号使用英文，隔开,如：V2.0,2019.10.12\
    3、remoteIp：可针对前端外网ip段进行灰度测试，多个ip段使用英文，隔开，如：127.0.0.1,192.168.\
    4、isOpen：是否开启灰度发布，true为开启，false为关闭
### 集成方式:
###### 1、Spring Boot 2.0.x.RELEASE+Spring Cloud Finchley版本集成方式，pom.xml文件增加引用如下：

```
<dependency>
    <groupId>com.runfast</groupId>
    <artifactId>gxptkc-discovery</artifactId>
    <version>4.0</version>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-parent</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```
###### 2、Spring Boot 2.1.x.RELEASE+Spring Cloud Greenwich版本集成方式，pom.xml文件增加引用如下：

```
<dependency>
    <groupId>com.runfast</groupId>
    <artifactId>gxptkc-discovery</artifactId>
    <version>5.0</version>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-parent</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```
###### 3、如果pom.xml文件存在下面引用，则将引用删除：
```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-alibaba-nacos-discovery</artifactId>
	<version>0.9.0.RELEASE</version>
</dependency>
```
###### 4、如果application.properties/application.yml 增加当前项目版本号内容：
```
spring.cloud.nacos.discovery.server-addr=192.168.2.168:8848
spring.cloud.nacos.discovery.metadata.version=1.0

或者

spring:    
  cloud:
      nacos:
        discovery:
          metadata:
            version: 1.0 # 项目版本号
          server-addr: 192.168.2.18:8848
```            