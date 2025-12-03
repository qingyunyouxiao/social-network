package com.qingyunyouxiao.sbsn.config;

import com.qingyunyouxiao.sbsn.dto.UserDto;
import com.qingyunyouxiao.sbsn.dto.CredentialsDto;
import com.qingyunyouxiao.sbsn.services.AuthenticationService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureAlgorithm;

import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


@Component
public class UserAuthenticationProvider {

    @Value("${security.jwt.token.secre-key:secret-key}")
    private String secretKey;

    private final AuthenticationService authenticationService;

    public UserAuthenticationProvider(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostConstruct
    protected void init() {
        // this is to avoid having the raw secret key available in the JVM
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String login) {
        Claims claims = Jwts.claims().setSubject(login);

        Date now = new Date();
        Date validity = new Date(now.getTime() + 3600000); // 1 hour

        return JwtBuilder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Authentication vaildateToken(String token) {
        String login = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        UserDto user = authenticationService.findByLogin(login);

        return new UsernamePasswordAuthenticationToken(user, null, Collection.emptyList());
    }

    public Authentication validateCredentials(CredentialsDto credentialsDto) {
        UserDto user = authenticationService.authenticate(credentialsDto);
        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    }
    
}
