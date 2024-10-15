package com.tochookpi.tochookpi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserAuthDTO {
    private String username;
    private String email;
    private String password;
}
