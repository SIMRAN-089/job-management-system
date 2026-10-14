
package com.simran.job_management.dto;

import com.simran.job_management.entity.UserRole;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class UserRequestDTO {

    @NotBlank
    private String name;
    @NotBlank @Email
    private String email;
    @NotBlank
    private String password;
    @NotNull
    private UserRole role;
}
