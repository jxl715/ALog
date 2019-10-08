

# ALog模块



####名词解释

ALog —— Android Log



### 设计目标

- 统一TAG前缀，后期调试可以通过关键字可以统一过滤  （当前TAG前缀为“ALog”)
- 设定默认TAG，采用默认TAG的地方写日志可以不用写TAG
- 自动保存日志到文件
- 摆脱默认Log类默认的TAG不能超过23个字符的限制
- 自动保存崩溃日志到本地文件，日志中包含机型、cpu架构、android版本号等信息
- 调试日志支持打印一些基本容器



### 架构

基于开源Logger日志库，添加自动保存崩溃日志到本地的功能，修改了保存日志对应的目录、文件名以及日志格式。



###使用

####初始化

```java
ALog.init(context);//建议在Application的onCreate中初始化
```

#### 打印日志

```java
 
 ALog.tag(“MainActivity”).d("onCreate");//指定TAG打印，实际TAG为"默认TAG-指定TAG"
 
 ALog.d("onCreate");//使用默认TAG打印
 ALog.i("xxxx");

 ALog.e("xxxx");
 Exception ex = new Exception("err msg");
 ALog.e(ex, "error");
 
 ArrayList<String> list=new ArrayList();
 list.add("hello");
 list.add("world");
 ALog.d(list);//只有调试日志debug支持

 String abc = "hahaha";
 ALog.d("abc=%s", abc);//支持通配符
 
 //json、xml格式化打印
 ALog.json(String json);
 ALog.xml(String xml);
```



#### 打印出来的日志效果

##### 控制台效果

![](img/log控制台显示效果.jpg)

#####文件效果

日志目录：/sdcard/Android/data/com.ubtech.jimupro/cache/log

文件名：yyyy-mm-dd_number.csv    

number-从0开始的数字，如果该文件大于500K（4000行左右）则，数字加1，新建一个新文件存储日志

单行日志打印格式：mm-dd  hh-mm-ss, level, tag, log content(日志具体内容)

示例：

2019-08-29_0.csv

```
08-29 17:19:22.167,D,Jimu-MainActivity,onCreate
08-29 17:19:27.832,D,Jimu,user denied storage permission
08-29 17:19:44.209,D,Jimu-MainActivity,onCreate
08-29 19:20:13.562,D,Jimu,user denied storage permission
08-29 19:21:22.268,D,Jimu-MainActivity,onCreate
08-29 19:21:31.865,D,Jimu-MainActivity,onCreate
08-29 19:34:05.817,D,Jimu-MainActivity,onCreate
08-29 19:34:10.640,D,Jimu,user denied storage permission
```

crash日志目录：/sdcard/Android/data/com.ubtech.jimupro/cache/crash

内容示例：

2019-08-29_20-37-30_1567082250087.txt

```
BOARD=walleye
CPU_ABI2=
HOST=wphr5.hot.corp.google.com
versionName=1.0
SUPPORTED_64_BIT_ABIS=[Ljava.lang.String;@8ce71dd
CPU_ABI=arm64-v8a
PERMISSIONS_REVIEW_REQUIRED=false
DISPLAY=PPR2.181005.003
SUPPORTED_ABIS=[Ljava.lang.String;@4860152
FINGERPRINT=google/walleye/walleye:9/PPR2.181005.003/4984323:user/release-keys
PRODUCT=walleye
ID=PPR2.181005.003
TYPE=user
SERIAL=unknown
DEVICE=walleye
TIME=1535603745000
MODEL=Pixel 2
MANUFACTURER=Google
USER=android-build
versionCode=1
BRAND=google
SUPPORTED_32_BIT_ABIS=[Ljava.lang.String;@72571b4
HARDWARE=walleye
IS_DEBUGGABLE=false
BOOTLOADER=mw8998-002.0072.00
RADIO=unknown
UNKNOWN=unknown
IS_EMULATOR=false
TAGS=release-keys
java.lang.RuntimeException: Unable to start activity ComponentInfo{com.ubtech.jimupro/com.ubtech.jimupro.MainActivity}: java.lang.NullPointerException: Attempt to invoke virtual method 'boolean java.util.ArrayList.isEmpty()' on a null object reference
	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2913)
	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:3048)
	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:78)
	at android.app.servertransaction.TransactionExecutor.executeCallbacks(TransactionExecutor.java:108)
	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:68)
	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1808)
	at android.os.Handler.dispatchMessage(Handler.java:106)
	at android.os.Looper.loop(Looper.java:193)
	at android.app.ActivityThread.main(ActivityThread.java:6669)
	at java.lang.reflect.Method.invoke(Native Method)
	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:493)
	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:858)
Caused by: java.lang.NullPointerException: Attempt to invoke virtual method 'boolean java.util.ArrayList.isEmpty()' on a null object reference
	at com.ubtech.jimupro.MainActivity.onCreate(MainActivity.java:34)
	at android.app.Activity.performCreate(Activity.java:7136)
	at android.app.Activity.performCreate(Activity.java:7127)
	at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1271)
	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2893)
	... 11 more
java.lang.NullPointerException: Attempt to invoke virtual method 'boolean java.util.ArrayList.isEmpty()' on a null object reference
	at com.ubtech.jimupro.MainActivity.onCreate(MainActivity.java:34)
	at android.app.Activity.performCreate(Activity.java:7136)
	at android.app.Activity.performCreate(Activity.java:7127)
	at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1271)
	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2893)
	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:3048)
	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:78)
	at android.app.servertransaction.TransactionExecutor.executeCallbacks(TransactionExecutor.java:108)
	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:68)
	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1808)
	at android.os.Handler.dispatchMessage(Handler.java:106)
	at android.os.Looper.loop(Looper.java:193)
	at android.app.ActivityThread.main(ActivityThread.java:6669)
	at java.lang.reflect.Method.invoke(Native Method)
	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:493)
	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:858)
```



### 修改默认配置
在ALog.class中init函数下修改默认配置
```java
FormatStrategy mFormatStrategy = PrettyFormatStrategy.newBuilder()
        .showThreadInfo(false)  //如果要显示线程信息则修改为true
        .methodCount(0)         //如果要显示方法调用的堆栈信息则修改为希望显示的堆栈层级数，典型值可以设为2
        .methodOffset(1)       
        .logStrategy(new LogcatLogStrategy()) 
        .tag(DEFAULT_TAG)  
        .build();
```



#### 注意事项

该项目中logger源码对应版本为V2.2.0。