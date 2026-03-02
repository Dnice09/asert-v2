package tz.go.mnrt.asert.modules.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ForgotPasswordDto {
    @NotNull
    private String email;
}
