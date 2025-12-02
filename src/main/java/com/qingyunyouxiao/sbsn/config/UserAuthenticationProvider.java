package com.qingyunyouxiao.sbsn.config;

import com.qingyunyouxiao.sbsn.dto.UserDto;
import com.qingyunyouxiao.sbsn.dto.CredentialsDto;

import io.jsonwebtoken.Jwts;

import java.util.Collections;
import com.qingyunyouxiao.sbsn.services.AuthenticationService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;



@Component
public class UserAuthenticationProvider {

    private final AuthenticationService authenticationService;

    public UserAuthenticationProvider(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public Authentication vaildateToken(String token) {
        String login = Jwts.parser();

        UserDto user = authenticationService.findByLogin(login);
    }

    public Authentication validateCredentials(CredentialsDto credentialsDto) {
        UserDto user = authenticationService.authenticate(credentialsDto);
        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    }
    
}
