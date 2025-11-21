# 核心解析（含代码问题修正）
### 1. 类与核心说明
这是一个 Spring Boot REST 控制器，负责用户模块的 HTTP 接口管理，提供用户资料查询、添加好友、用户搜索功能。
核心注解：`@RestController`（标记为 REST 接口类）、`@RequestMapping("/v1/uers")`（基础路径，注意存在拼写错误）、`UserService`（业务逻辑依赖，通过构造函数注入）。

### 2. 接口详情（翻译 + 功能 + 问题修正）
| 接口方法                         | 关键参数 / 返回值                                            | 代码问题修正                                                 |
| -------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| `GET /v1/uers/{userId}/profile`  | - 路径参数：`userId`（用户 ID，int 类型）- 返回：`ProfileDto`（用户资料 DTO，HTTP 200） | 无语法错误                                                   |
| `POST /v1/uers/friends/friendId` | - 路径参数：`friendId`（好友 ID，Long 类型）- 返回：`Void`（无响应体，HTTP 204 No Content） | 1. 路径错误：`friendId`应为占位符`{friendId}`，否则无法接收参数；2. 逻辑缺失：未接收当前登录用户 ID（需补充令牌解析或用户上下文） |
| `POST /v1/uers`                  | - 查询参数：`term`（搜索关键词，必填）- 返回：`List<UserSummaryDto>`（用户摘要列表，HTTP 200） | 1. 路径不规范：搜索接口建议改为`/v1/users/search`（语义更清晰）；2. 方法语义冲突：`POST`通常用于创建资源，搜索建议用`GET`（符合 RESTful 规范） |

### 3. 核心问题汇总（影响功能运行的关键错误）
1. 基础路径拼写错误：@RequestMapping("/v1/uers") 应为 /v1/users（"users" 复数形式，拼写正确）；

2. 路径参数占位符缺失：addFriend接口的路径 friends/friendId 应改为 friends/{friendId}，否则@PathVariable Long friendId无法获取参数；
3. HTTP 方法语义不规范：搜索功能用POST不符合 RESTful 设计，建议改为GET /v1/users/search?term=xxx；
4. 用户上下文缺失：addFriend接口未传递当前登录用户 ID（需通过 Token 解析或 Session 获取，否则无法确定 “谁添加好友”）。

### 4. 修正后的核心代码片段（关键部分）
```java
// 修正1：基础路径拼写
@RequestMapping("/v1/users")
public class UserController {

    // 修正2：添加好友接口路径+方法规范
    @PostMapping("/friends/{friendId}")
    public ResponseEntity<Void> addFriend(
            @RequestHeader("Authorization") String token, // 接收登录令牌（解析当前用户ID）
            @PathVariable Long friendId) {
        Long currentUserId = parseUserIdFromToken(token); // 从令牌解析当前登录用户ID（需实现）
        userService.addFriend(currentUserId, friendId); // 补充当前用户ID参数
        return ResponseEntity.noContent().build();
    }

    // 修正3：搜索接口改为GET，路径语义化
    @GetMapping("/search")
    public ResponseEntity<List<UserSummaryDto>> searchUsers(@RequestParam(value = "term") String term) {
        return ResponseEntity.ok(userService.searchUsers(term));
    }
}
```
运行