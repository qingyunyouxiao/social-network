# 正确的命令是 mvn clean package

从你提供的终端输出可以看到，这是Maven 执行mvn package clean（实际是先package再clean）的完整构建流程，虽然命令顺序写反了（通常是mvn clean package先清理再打包），但最终还是成功了。我把每一段关键输出拆解解释：

### 项目初始化阶段

```plaintext
[INFO] Scanning for projects...
[INFO] -------------------< com.qingyunyouxiao:sbsn >-------------------
[INFO] Building sbsn 0.0.1-SNAPSHOT
[INFO]   from pom.xml
[INFO] -------------------[ jar ]-------------------
```

含义：Maven 扫描到项目，识别出项目的groupId（com.qingyunyouxiao）、artifactId（sbsn）、版本（0.0.1-SNAPSHOT，快照版），并确定项目打包类型是jar（Java 普通项目）。

### 资源复制阶段（resources）

```plaintext
[INFO] --- resources:3.3.1:resources (default-resources) @ sbsn ---
[INFO] Copying 1 resource from src\main\resources to target\classes
[INFO] Copying 0 resource from src\main\resources to target\classes
```

含义：Maven 调用resources插件（版本 3.3.1），把项目src/main/resources目录下的1 个资源文件（比如配置文件application.yml）复制到编译目录target/classes里（第二个 “Copying 0” 是重复日志，忽略即可）。

### 代码编译阶段（compile）

```plaintext
[INFO] --- compiler:3.14.1:compile (default-compile) @ sbsn ---
[INFO] Recompiling the module because of changed source code.
[INFO] Compiling 9 source files with javac [debug parameters release 21] to target\classes
[INFO] 由于在类路径中发现了一个或多个处理程序，因此启用了注解处理...
```

含义：Maven 调用compiler插件（版本 3.14.1），因为源代码有修改，所以重新编译src/main/java下的9 个 Java 文件，使用的 JDK 版本是 21（release 21），编译结果输出到target/classes。
后面的注解处理提示：是 JDK 21 的新特性提醒（未来版本可能默认禁用注解处理），不影响构建，只是警告。

### 测试资源 / 测试编译阶段

```plaintext
[INFO] --- resources:3.3.1:testResources (default-testResources) @ sbsn ---
[INFO] skip non existing resourceDirectory D:\Projects\social-network\src\test\resources
[INFO] --- compiler:3.14.1:testCompile (default-testCompile) @ sbsn ---
[INFO] Recompiling the module because of changed dependency.
[INFO] Compiling 1 source file with javac [debug parameters release 21] to target\test-classes
```

测试资源：src/test/resources目录不存在，所以跳过复制。
测试编译：因为依赖有变化，重新编译src/test/java下的1 个测试类（比如SbsnApplicationTests），编译结果输出到target/test-classes。

### 测试执行阶段（test）

```plaintext
[INFO] --- surefire:3.5.4:test (default-test) @ sbsn ---
[INFO] Using auto detected provider org.apache.maven.surefire.junitplatform.JUnitPlatformProvider
Downloading from central: https://repo.maven.apache.org/...
Downloaded from central: https://repo.maven.apache.org/...
[INFO] Running com.qingyunyouxiao.sbsn.SbsnApplicationTests
08:45:16.730 [main] INFO ... Could not detect default configuration classes...
08:45:16.851 [main] INFO ... Found @SpringBootConfiguration com.qingyunyouxiao.sbsn.SbsnApplication...
:: Spring Boot ::        (v3.5.7)
2025-11-21T08:45:17.313+08:00 INFO 15736 --- [main] c.q.sbsn.SbsnApplicationTests : Starting SbsnApplicationTests using Java 21.0.6...
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.483 s
```

含义：Maven 调用surefire测试插件（版本 3.5.4），自动选择 JUnit 平台执行测试：
先下载了测试需要的依赖（比如surefire-junit-platform）；
执行SbsnApplicationTests这个测试类；
Spring Boot 启动日志（版本 3.5.7），测试用 Java 21.0.6 运行；
测试结果：运行了 1 个测试，0 失败、0 错误、0 跳过，执行时间 2.483 秒。

### 打包阶段（jar + repackage）

```plaintext
[INFO] --- jar:3.4.2:jar (default-jar) @ sbsn ---
[INFO] Building jar: D:\Projects\social-network\target\sbsn-0.0.1-SNAPSHOT.jar
[INFO] --- spring-boot:3.5.7:repackage (repackage) @ sbsn ---
[INFO] Replacing main artifact ... with repackaged archive, adding nested dependencies in BOOT-INF/.
[INFO] The original artifact has been renamed to ...sbsn-0.0.1-SNAPSHOT.jar.original
```

含义：jar插件：先打包出一个原始 jar 包（sbsn-0.0.1-SNAPSHOT.jar）；
spring-boot插件：对原始 jar 包进行重新打包（Spring Boot 项目的关键步骤），把所有依赖嵌入到 jar 包的BOOT-INF/lib目录下，生成可直接运行的 “胖 jar”；
原始 jar 包被重命名为.original后缀（作为备份）。

### 清理阶段（clean）

```plaintext
[INFO] --- clean:3.4.1:clean (default-clean) @ sbsn ---
[INFO] Deleting D:\Projects\social-network\target
```

含义：Maven 调用clean插件，删除target目录（因为你命令写的是mvn package clean，所以是先打包再清理，这会导致刚生成的 jar 包被删掉！正常应该用mvn clean package先清理再打包）。

### 构建结果

```plaintext
[INFO] BUILD SUCCESS
[INFO] Total time:  9.272 s
[INFO] Finished at: 2025-11-21T08:45:20+08:00
```

含义：整个构建流程成功完成，总耗时 9.272 秒。
注意点（小问题）
你执行的命令是mvn package clean，导致打包完成后又执行了 clean，把 target 目录删掉了（所以刚生成的 jar 包会消失）。正确的命令应该是mvn clean package（先清理旧的 target，再打包，最终保留 jar 包）。