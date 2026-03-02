package tz.go.mnrt.asert.modules.passwordresettoken.dtos;


import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetDto {
    @NotNull(message = "Token is required")
    private String token;
    @NotNull(message = "Password is required")
    private String newPassword;
}
