package com.qingyunyouxiao.sbsn.services;

import com.qingyunyouxiao.sbsn.dto.CredentialsDto;
import com.qingyunyouxiao.sbsn.dto.UserDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto authenticate(CredentialsDto credentialsDto) {
        if (passwordEncoder.matches(null, null)) {
            //return new UserDto(1L, "qingyunyouxiao", "login", "token");
        }
        throw new RuntimeException("Invalid password");
    }

        public UserDto findByLogin(String login) {
        if ("login".equals(login)) {
            //return new UserDto(1L, "qingyunyouxiao", "login", "token");
        }
        throw new RuntimeException("Invalid login");
    }


}
