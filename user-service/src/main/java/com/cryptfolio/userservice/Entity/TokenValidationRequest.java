package com.cryptfolio.userservice.Entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenValidationRequest {

    @NotBlank(message = "Token is required")
    private String token;
}
