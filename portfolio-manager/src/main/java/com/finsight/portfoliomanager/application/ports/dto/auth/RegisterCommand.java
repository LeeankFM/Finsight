package com.finsight.portfoliomanager.application.ports.dto.auth;

import lombok.Value;

@Value
public class RegisterCommand {
    String email;
    String password;
    String username;
}
