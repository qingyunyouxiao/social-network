我来帮你完全修改好代码，解决所有依赖和语法错误，适配最新版 JJWT 0.11.5，同时保持你的原有逻辑不变：

### 第一步：先确认依赖（必须添加）
如果是 Maven 项目，在 `pom.xml` 中添加以下依赖（如果是 Gradle 可以告诉我，我给你对应的依赖）：
```xml
<!-- JJWT 核心依赖（最新稳定版） -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

### 第二步：修改后的完整代码（直接复制使用）
```java
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import javax.annotation.PostConstruct;
import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

// 假设这些是你的自定义类（如果包路径不同，需要修改导入）
import your.package.path.AuthenticationService; // 替换为你 AuthenticationService 的实际包路径
import your.package.path.UserDto; // 替换为你 UserDto 的实际包路径
import your.package.path.CredentialsDto; // 替换为你 CredentialsDto 的实际包路径

@Component
public class UserAuthenticationProvider {

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey; // 配置文件中的密钥（字符串）

    private Key signingKey; // 真正用于签名的 Key 类型（新版本要求）

    private final AuthenticationService authenticationService;

    // 构造函数注入（Spring 会自动注入 AuthenticationService）
    public UserAuthenticationProvider(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostConstruct
    protected void init() {
        // 1. 解码 Base64 密钥（因为你原有代码做了 Base64 编码，这里保持一致）
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        // 2. 将字节数组转换为 HS256 要求的 Key 类型（确保密钥至少 32 字节 = 256 位）
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // 生成 Token（修复签名方式，适配新版本）
    public String createToken(String login) {
        Claims claims = Jwts.claims().setSubject(login);

        Date now = new Date();
        Date validity = new Date(now.getTime() + 3600 * 1000); // 1 小时有效期

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(signingKey, SignatureAlgorithm.HS256) // 先传 Key，再指定算法（新版本要求）
                .compact();
    }

    // 修复拼写错误：vaildateToken → validateToken（重要！）
    public Authentication validateToken(String token) {
        try {
            // 1. 构建 JwtParser（新版本必须通过 parserBuilder() 构建）
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(signingKey) // 设置签名密钥
                    .build();

            // 2. 解析 Token（parseSignedClaims 替代旧版本的 parseClaimsJws）
            Jws<Claims> jwsClaims = parser.parseSignedClaims(token);
            String login = jwsClaims.getBody().getSubject();

            // 3. 通过登录名查询用户
            UserDto user = authenticationService.findByLogin(login);

            // 4. 返回认证信息
            return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());

        } catch (ExpiredJwtException e) {
            throw new RuntimeException("JWT Token 已过期", e);
        } catch (MalformedJwtException e) {
            throw new RuntimeException("JWT Token 格式错误", e);
        } catch (SignatureException e) {
            throw new RuntimeException("JWT Token 签名无效", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("JWT Token 为空或无效", e);
        } catch (Exception e) {
            throw new RuntimeException("JWT Token 验证失败", e);
        }
    }

    // 验证用户名密码（原有代码不变，保持正常使用）
    public Authentication validateCredentials(CredentialsDto credentialsDto) {
        UserDto user = authenticationService.authenticate(credentialsDto);
        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    }
}
```

### 第三步：关键修改说明（帮你避坑）
1. **修复拼写错误**：`vaildateToken` → `validateToken`（之前的方法名少写了一个 `d`，会导致调用时找不到方法）
2. **密钥处理优化**：
   - 原有代码对密钥做了 Base64 编码，所以在 `init()` 方法中先解码，再转换为 `Key` 类型
   - 确保密钥长度足够：HS256 要求密钥至少 32 字节（256 位），如果你的配置文件中 `secret-key` 太短，会报错！建议在配置文件中设置一个 32 字节以上的密钥（比如生成一个随机字符串再 Base64 编码）
3. **适配新版本 API**：
   - `Jwts.parser()` → `Jwts.parserBuilder().build()`（新版本必须这样构建解析器）
   - `parseClaimsJws(token)` → `parseSignedClaims(token)`（新版本方法名变更）
   - `signWith(SignatureAlgorithm.HS256, secretKey)` → `signWith(signingKey, SignatureAlgorithm.HS256)`（新版本要求先传 `Key` 类型，再指定算法）
4. **添加异常处理**：捕获了 Token 过期、格式错误、签名无效等常见异常，方便排查问题

### 第四步：配置文件说明（application.properties 或 application.yml）
确保你的配置文件中有密钥配置（建议替换默认的 `secret-key` 为自定义的长密钥）：
```properties
# application.properties
security.jwt.token.secret-key=你的Base64编码后的32字节以上密钥
```
比如生成一个 32 字节的随机密钥并 Base64 编码（可以用 Java 代码生成）：
```java
// 生成 32 字节随机密钥并 Base64 编码（用于配置文件）
public class KeyGenerator {
    public static void main(String[] args) {
        byte[] keyBytes = new byte[32]; // 32 字节 = 256 位
        new java.security.SecureRandom().nextBytes(keyBytes);
        String base64Key = Base64.getEncoder().encodeToString(keyBytes);
        System.out.println("Base64 编码后的密钥：" + base64Key);
    }
}
```
运行这段代码，把输出的 Base64 字符串复制到配置文件中，替换默认的 `secret-key`。

### 第五步：验证是否正常工作
1. 启动项目，确保没有依赖报错（如果提示找不到 `AuthenticationService`、`UserDto` 等类，需要修改代码中的导入路径，替换为你实际的包路径）
2. 调用 `createToken("用户名")` 生成 Token
3. 调用 `validateToken("生成的Token")` 验证 Token，能正常返回 `Authentication` 对象即为成功

这样修改后，之前的两个错误（`parseClaimsJws` 未定义、`HS256` 无法解析）都会彻底解决，代码可以直接投入使用！如果还有其他问题（比如依赖冲突、密钥长度不够），可以随时告诉我。