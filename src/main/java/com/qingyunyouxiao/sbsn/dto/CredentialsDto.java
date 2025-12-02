package com.qingyunyouxiao.sbsn.dto;

public class CredentialsDto {
    
    private String login;
    private char[] password;


    public CredentialsDto(String login, char[] password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public char[] getPassword() {
        return password;
    }
}
