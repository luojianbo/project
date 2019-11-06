controller切面日志集成。
====

### 集成方式:

```
<!--  controller切面日志输入  -->
<dependency>
    <groupId>com.runfast</groupId>
    <artifactId>gxptkc-aop-log</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 日志输出内容格式例子:
[类名.方法名.sessionId]

```
[EquipmentController.adminList.f8160a75-4994-4d57-abb9-b51d07c10055]RequestHeader : method=GET;remoteIp=192.168.2.173;clientId=400001;clientVersion=V1.0;token=xxx;
[EquipmentController.adminList.f8160a75-4994-4d57-abb9-b51d07c10055]RequestBody   : {"size":["10"],"current":["1"]};
[EquipmentController.adminList.f8160a75-4994-4d57-abb9-b51d07c10055]ResponseBody  : {"data":{"total":0,"current":1,"data":[],"size":10},"success":true,"message":"success","status":200}

```