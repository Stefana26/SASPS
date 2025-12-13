package com.sasps.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateTokenResponse {
    private Boolean valid;
    private String username;
    private String message;

    public ValidateTokenResponse(Boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }
}
