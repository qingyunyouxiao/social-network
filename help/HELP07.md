没问题！我会用「通俗语言+代码逐行拆解+核心概念对比」的方式，把 JJWT 新版 API（0.11.x+）的核心逻辑讲透——重点解释「为什么要这么写」「每个组件的作用」，以及和旧版的区别，让你不仅会用，还懂原理。

### 先明确 3 个核心概念（基础中的基础）
在讲代码前，必须先搞懂 JWT 的核心组成和 JJWT 的 API 设计思路，否则看代码只会懵：

#### 1. JWT 是什么？（JSON Web Token）
本质是一串「加密的字符串」，用于在系统间安全传递用户信息（比如登录后给前端返回 Token，前端后续请求带着 Token 证明身份）。

JWT 由 3 部分组成（用 `.` 分隔）：
- **Header（头部）**：告诉系统「用什么算法签名」（比如 HS256）；
- **Payload（载荷）**：存放实际数据（比如用户名、过期时间，叫「Claims」）；
- **Signature（签名）**：用密钥对 Header+Payload 加密，防止数据被篡改。

#### 2. JJWT 的 API 设计思路（新版 vs 旧版）
- 旧版（0.9.x）：API 松散，比如 `Jwts.claims()` 直接返回 `Claims`，`signWith()` 允许传简单字符串密钥，容易出错；
- 新版（0.11.x+）：**强制用「构建器模式」**，把「创建对象」和「设置属性」分开，同时加强安全性（比如强制合规密钥）。
  - 构建器（Builder）：是「半成品对象」，用于链式设置属性（比如 `ClaimsBuilder` 专门设置载荷，`TokenBuilder` 专门构建 Token）；
  - 最终实例：必须调用 `build()` 或 `compact()` 才能得到（比如 `ClaimsBuilder.build()` 得到 `Claims`，`TokenBuilder.compact()` 得到 Token 字符串）。

#### 3. 核心组件对应关系
| JJWT 组件          | 作用                                  | 最终产物          |
|---------------------|---------------------------------------|-------------------|
| `ClaimsBuilder`     | 构建 JWT 的「载荷（Payload）」         | `Claims`（载荷实例） |
| `TokenBuilder`      | 整合 Header+Payload+Signature，构建 Token | 字符串（JWT Token） |
| `JwtParserBuilder`  | 解析 Token，验证签名和有效性          | `Jws<Claims>`（解析结果） |


### 逐行拆解核心代码（分「构建 Token」和「解析 Token」两部分）
以之前给的 0.12.x 代码为例，逐行解释「为什么这么写」「作用是什么」：

#### 一、构建 JWT Token（最常用场景：登录成功后生成 Token）
```java
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

// 1. 生成安全的密钥（核心：新版强制要求合规密钥）
SecretKey secretKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);

// 2. 构建 JWT Token（链式调用：TokenBuilder → 字符串）
String token = Jwts.builder()
    .setSubject("user123") // 标准载荷：用户名/用户ID（Subject 是 JWT 标准字段）
    .setIssuedAt(new Date()) // 标准载荷：Token 签发时间
    .setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000)) // 标准载荷：过期时间（1小时）
    .claim("role", "admin") // 自定义载荷：额外的业务字段（比如用户角色）
    .signWith(secretKey) // 签名：用合规密钥对 Header+Payload 加密
    .compact(); // 关键：生成最终的 JWT Token 字符串
```

##### 逐行解释：
1. **生成密钥：`SecretKey secretKey = Keys.secretKeyFor(HS256);`**
   - 作用：创建 JWT 签名用的「密钥」（相当于加密/解密的「钥匙」）；
   - 为什么这么写？
     - 旧版可以直接传字符串（比如 `signWith(HS256, "secret")`），但「secret」只有 6 字节，不符合 HS256 算法的 256 位（32 字节）要求，不安全；
     - 新版 `Keys.secretKeyFor(HS256)` 会自动生成「32 字节的随机密钥」，完全合规，避免手动生成出错；
   - 注意：实际开发中，密钥不能硬编码！要放在配置文件（比如 `application.yml`）中，比如：
     ```java
     // 从配置文件读取密钥（推荐写法）
     String secret = "${jwt.secret:默认32字节密钥（仅开发用）}";
     SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
     ```

2. **创建 Token 构建器：`Jwts.builder()`**
   - 作用：返回 `TokenBuilder` 实例（构建 Token 的「半成品」），后续所有设置都基于这个构建器；
   - 为什么不用 `new` 关键字？JJWT 用「静态工厂方法」封装了复杂的创建逻辑，简化调用。

3. **设置标准载荷：`setSubject("user123")`**
   - 作用：设置 JWT 标准字段 `subject`（通常存用户名、用户 ID，是 Token 的核心标识）；
   - 什么是「标准载荷」？JWT 定义了一批常用字段（比如 `iss` 签发者、`exp` 过期时间、`iat` 签发时间），`setXXX()` 方法是专门设置这些标准字段的快捷方式。

4. **设置过期时间：`setExpiration(...)`**
   - 作用：指定 Token 的有效期（这里是 1 小时），过期后解析会报错，避免 Token 被永久滥用；
   - 原理：`new Date(System.currentTimeMillis() + 3600 * 1000)` 表示「当前时间 + 3600 秒（1小时）」。

5. **设置自定义载荷：`claim("role", "admin")`**
   - 作用：添加业务相关的自定义字段（比如用户角色、部门），解析后可以拿到这些数据；
   - 和 `setSubject()` 的区别：`setSubject()` 是标准字段的快捷方法，`claim(key, value)` 可以设置任意自定义字段（key 是字符串，value 是任意对象）。

6. **签名：`signWith(secretKey)`**
   - 作用：用之前生成的密钥，对「Header+Payload」进行加密，生成「Signature」部分；
   - 为什么重要？签名是 JWT 安全的核心——如果有人篡改 Token 的 Payload（比如把 `role` 从 `user` 改成 `admin`），解析时会发现「签名不匹配」，直接拒绝。

7. **生成 Token：`compact()`**
   - 作用：将 `TokenBuilder`（半成品）转为最终的「JWT 字符串」（格式：Header.Payload.Signature）；
   - 注意：必须调用这个方法！否则只是个构建器，不会生成实际 Token。


#### 二、解析 JWT Token（最常用场景：验证前端请求的 Token 有效性）
```java
// 解析 Token 并获取载荷（Claims）
Claims claims = Jwts.parserBuilder()
    .setSigningKey(secretKey) // 关键：用和构建时相同的密钥验证签名
    .build() // 生成 JwtParser 实例（解析器）
    .parseClaimsJws(token) // 解析 Token，验证签名和有效性（比如是否过期）
    .getBody(); // 从解析结果中获取载荷（Claims）

// 读取载荷中的数据
String subject = claims.getSubject(); // 读取标准字段：用户名（"user123"）
String role = claims.get("role", String.class); // 读取自定义字段：角色（"admin"）
Date expiration = claims.getExpiration(); // 读取标准字段：过期时间
```

##### 逐行解释：
1. **创建解析器构建器：`Jwts.parserBuilder()`**
   - 作用：返回 `JwtParserBuilder` 实例（构建解析器的「半成品」），用于设置解析规则（比如密钥、过期时间容忍度）；
   - 为什么用构建器？解析 Token 可能需要多个配置（比如密钥、自定义时间戳验证），用构建器链式设置更清晰。

2. **设置验证密钥：`setSigningKey(secretKey)`**
   - 作用：告诉解析器「用这个密钥验证签名」；
   - 核心原则：**必须和构建 Token 时用的密钥完全一致**！否则会抛出「签名不匹配」异常（`SignatureException`），防止 Token 被篡改。

3. **生成解析器：`build()`**
   - 作用：将 `JwtParserBuilder`（半成品）转为 `JwtParser` 实例（真正的解析器）；
   - 注意：和 `ClaimsBuilder.build()` 逻辑一致——构建器必须调用 `build()` 才能得到最终实例。

4. **解析 Token：`parseClaimsJws(token)`**
   - 作用：解析 Token 字符串，做 3 件关键事：
     1. 验证签名：确保 Token 没被篡改；
     2. 验证有效性：检查 Token 是否过期（`exp` 字段）、是否还没生效（如果设置了 `nbf` 字段）；
     3. 解析出 Header 和 Payload；
   - 返回值：`Jws<Claims>` 是「带签名的 JWT 解析结果」，包含 Header（`getHeader()`）、Payload（`getBody()`）、Signature（`getSignature()`）；
   - 异常情况：如果签名不匹配、Token 过期、格式错误，都会抛出对应的异常（比如 `SignatureException`、`ExpiredJwtException`），可以捕获这些异常做业务处理（比如返回「Token 无效」「Token 已过期」）。

5. **获取载荷：`getBody()`**
   - 作用：从解析结果中拿到 `Claims` 实例（也就是 Token 中的 Payload 数据）；
   - 后续操作：用 `claims.getXXX()` 方法读取字段——标准字段有快捷方法（`getSubject()`、`getExpiration()`），自定义字段用 `get(key, 类型)`（比如 `get("role", String.class)`）。


#### 三、手动创建 Claims（场景：需要批量设置载荷时）
有时候载荷字段多，想先单独构建 `Claims`，再传入 Token 构建器，代码如下：
```java
// 1. 构建 Claims（ClaimsBuilder → Claims）
Claims claims = Jwts.claims()
    .setSubject("user123") // 标准字段
    .setIssuedAt(new Date()) // 标准字段
    .put("role", "admin") // 自定义字段（和 claim() 效果一样）
    .put("dept", "IT") // 更多自定义字段
    .build(); // 关键：生成 Claims 实例（否则是 ClaimsBuilder，会报类型不匹配）

// 2. 将 Claims 传入 Token 构建器
String token = Jwts.builder()
    .setClaims(claims) // 直接传入已构建好的 Claims
    .setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000))
    .signWith(secretKey)
    .compact();
```

##### 关键解释：
- `Jwts.claims()` 返回 `ClaimsBuilder`（构建器），必须调用 `build()` 才能得到 `Claims` 实例——这就是你之前遇到「Type mismatch: cannot convert from ClaimsBuilder to Claims」错误的根本原因！
- `put(key, value)` 和 `claim(key, value)` 效果完全一样，只是 `put()` 是 `ClaimsBuilder` 的方法，`claim()` 是 `TokenBuilder` 的方法，都是设置载荷字段。


### 旧版 vs 新版 API 对比（帮你彻底告别旧思维）
| 功能                | 旧版（0.9.x）写法                          | 新版（0.12.x）写法                          | 核心区别                                  |
|---------------------|---------------------------------------------|---------------------------------------------|-------------------------------------------|
| 生成密钥            | `signWith(HS256, "secret")`（字符串密钥）   | `Keys.secretKeyFor(HS256)`（合规密钥）      | 新版强制密钥合规，更安全                  |
| 创建 Claims         | `Claims claims = Jwts.claims()`（直接返回） | `Claims claims = Jwts.claims().build()`     | 新版需要 `build()`，区分构建器和实例      |
| 构建 Token          | `Jwts.builder().signWith(HS256, "secret").compact()` | `Jwts.builder().signWith(secretKey).compact()` | 新版用 `SecretKey` 签名，旧版用字符串     |
| 解析 Token          | `Jwts.parser().setSigningKey("secret").parseClaimsJws(token)` | `Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)` | 新版用解析器构建器，配置更灵活            |


### 最后总结 3 个关键结论（记牢就不会踩坑）
1. **构建器必须「收尾」**：`ClaimsBuilder` 要 `build()` 成 `Claims`，`TokenBuilder` 要 `compact()` 成 Token 字符串，`JwtParserBuilder` 要 `build()` 成解析器；
2. **密钥必须「合规」**：HS256 用 32 字节密钥，推荐 `Keys.secretKeyFor()` 生成，不要硬编码；
3. **解析必须「验签」**：`setSigningKey()` 必须和构建时的密钥一致，否则 Token 无效。

这样一来，你不仅知道代码怎么写，还懂每个步骤的原理，后续遇到类似问题也能自己排查啦！