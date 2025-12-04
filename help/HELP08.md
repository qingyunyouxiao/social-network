### 核心修改点：
- 解析 Token 时：`Jwts.parser()` → `Jwts.parserBuilder()`，并添加 `build()`，使用 `parseSignedClaims()` 替代 `parseClaimsJws()`。
- 确保 `secretKey` 是 `Key` 类型（新版本推荐，而非字符串）。

修正后的完整代码：
```java
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.util.Collections;
import java.util.Date;
import java.security.Key;

// 假设你的类和依赖注入（如 authenticationService、secretKey）已正确配置
public class JwtTokenProvider {

    private final AuthenticationService authenticationService;
    private final Key secretKey; // 注意：这里必须是 Key 类型，而非 String
    private static final long VALIDITY_PERIOD = 3600 * 1000; // 1小时

    // 构造函数（依赖注入）
    public JwtTokenProvider(AuthenticationService authenticationService, String secretKeyString) {
        this.authenticationService = authenticationService;
        // 将字符串密钥转换为 Key 类型（HS256 要求密钥长度至少 256 位 = 32 字节）
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    // 生成 Token（修正后，兼容新版本）
    public String createToken(String login) {
        Claims claims = Jwts.claims().setSubject(login);

        Date now = new Date();
        Date validity = new Date(now.getTime() + VALIDITY_PERIOD);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256) // 先传 Key，再指定算法
                .compact();
    }

    // 验证 Token（核心修正：parserBuilder() + build() + parseSignedClaims()）
    public Authentication validateToken(String token) {
        // 1. 构建 JwtParser（新版本必须通过 parserBuilder() 构建）
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(secretKey) // 设置签名密钥
                .build(); // 生成 JwtParser

        // 2. 解析 Token（parseSignedClaims() 替代旧的 parseClaimsJws()）
        Jws<Claims> jwsClaims = parser.parseSignedClaims(token);
        String login = jwsClaims.getBody().getSubject();

        // 3. 验证用户并返回 Authentication
        UserDto user = authenticationService.findByLogin(login);
        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    }
}
```


### 关键注意点
1. **密钥类型转换**：  
   新版本 JJWT 推荐使用 `Key` 类型（而非字符串）作为密钥，通过 `Keys.hmacShaKeyFor(secretKeyBytes)` 转换。  
   ⚠️ 重要：HS256 算法要求密钥长度 **至少 256 位（32 字节）**，如果你的密钥字符串太短，会抛出 `IllegalKeyException`，请确保密钥足够长（建议生成 32 字节以上的随机密钥）。

2. **异常处理（可选但推荐）**：  
   解析 Token 时可能抛出多种异常（如过期、签名无效、格式错误等），建议添加异常处理：
   ```java
   public Authentication validateToken(String token) {
       try {
           JwtParser parser = Jwts.parserBuilder()
                   .setSigningKey(secretKey)
                   .build();
           Jws<Claims> jwsClaims = parser.parseSignedClaims(token);
           String login = jwsClaims.getBody().getSubject();
           UserDto user = authenticationService.findByLogin(login);
           return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
       } catch (ExpiredJwtException e) {
           throw new RuntimeException("Token 已过期");
       } catch (MalformedJwtException e) {
           throw new RuntimeException("Token 格式错误");
       } catch (SignatureException e) {
           throw new RuntimeException("Token 签名无效");
       } catch (IllegalArgumentException e) {
           throw new RuntimeException("Token 为空");
       }
   }
   ```
