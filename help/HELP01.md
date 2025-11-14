# VS code 开发springboot程序常用操作


#### 常用的 Spring Boot 操作

以下是一些常见的 Spring Boot 项目操作，帮助你快速进行开发、构建、调试和运行。

#### 1. **调出 VSCode 的终端（命令行）**

在 VSCode 中，调出终端（命令行）的方法：

- **快捷键打开终端**：按 `Ctrl + `或 `Ctrl + Shift + ~` 可以快速调出 VSCode 内置终端。
- **通过命令面板调出终端**：
  1. 按 `Ctrl + Shift + P` 调出命令面板。
  2. 输入 `Terminal: Create New Terminal` 或直接输入 `终端`，然后按 `Enter`，即会打开一个新终端。

#### 2. **启动和停止 Spring Boot 应用程序**

- **通过 Maven 启动**：

  ```bash
  mvn spring-boot:run
  ```

- **通过 Gradle 启动**：

  ```bash
  gradle bootRun
  ```

- **通过 Java 文件启动**： 打开主类（带有 `@SpringBootApplication` 注解的类）并点击运行按钮，或按 `F5` 进行调试启动。

- **通过 Jar 文件启动**： 打包生成 `.jar` 文件后，可以使用以下命令运行应用：

  ```bash
  java -jar target/your-app-name.jar
  ```

#### 3. **编译和构建项目**

- **Maven 编译项目**：

  ```bash
  mvn clean compile
  ```

- **Gradle 编译项目**：

  ```bash
  gradle clean build
  ```

- **Maven 打包项目**：

  ```bash
  mvn clean package
  ```

- **Gradle 打包项目**：

  ```bash
  gradle clean assemble
  ```

#### 4. **清理项目**

- **Maven 清理项目**：

  ```bash
  mvn clean
  ```

- **Gradle 清理项目**：

  ```bash
  gradle clean
  ```

#### 5. **查看项目依赖树**

- **Maven 查看依赖树**：

  ```bash
  mvn dependency:tree
  ```

- **Gradle 查看依赖树**：

  ```bash
  gradle dependencies
  ```

#### 6. **使用 Spring Boot DevTools 实现热部署**

添加 DevTools 依赖，自动热部署应用：

- **Maven**：

  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-devtools</artifactId>
  </dependency>
  ```

- **Gradle**：

  ```groovy
  developmentOnly 'org.springframework.boot:spring-boot-devtools'
  ```

#### 7. **测试和验证项目**

- **Maven 运行测试**：

  ```bash
  mvn test
  ```

- **Gradle 运行测试**：

  ```bash
  gradle test
  ```

#### 8. **管理 Spring Boot 项目日志**

在 `application.yml` 或 `application.properties` 中修改日志级别：

```yaml
logging:
  level:
    root: info
    com.yourpackage: debug
```

#### 9. **运行 SQL 数据库迁移**

- **Flyway 数据库迁移**：

  ```bash
  mvn flyway:migrate
  ```

- **Liquibase 数据库迁移**：

  ```bash
  mvn liquibase:update
  ```

#### 10. **通过 Actuator 监控和管理 Spring Boot 应用**

- 在 `application.yml` 中启用 Actuator：

  ```yaml
  management:
    endpoints:
      web:
        exposure:
          include: "*"
  ```

  然后访问 `/actuator` 端点获取应用状态和监控信息。

#### 11. **使用 Swagger 生成 API 文档**

添加 Swagger 依赖并访问 API 文档：

- **Maven**：

  ```xml
  <dependency>
      <groupId>io.springfox</groupId>
      <artifactId>springfox-boot-starter</artifactId>
      <version>3.0.0</version>
  </dependency>
  ```

访问 `http://localhost:8080/swagger-ui.html` 查看生成的 API 文档。

#### 12. **调试 Spring Boot 应用**

- 在 VSCode 中按 `F5` 启动调试模式。
- 你可以添加断点，并查看变量和调用栈。

------

通过这些常用操作，你可以高效地在 VSCode 中进行 Spring Boot 开发。