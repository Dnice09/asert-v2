package tz.go.mnrt.asert.modules.passwordresettoken.dtos;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetTokenRequestDto {
    private Long id;
    private UUID uuid;

    @NotNull(message = "user is required")
    private Integer userId;

    @NotNull(message = "token is required")
    private String token;
    @NotNull(message = "Date is required")
    private LocalDateTime expiryDate;
}
