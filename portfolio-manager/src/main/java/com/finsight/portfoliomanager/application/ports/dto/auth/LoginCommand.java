package com.finsight.portfoliomanager.application.ports.dto.auth;

import lombok.Value;

@Value
public class LoginCommand {
    String email;
    String password;
}
