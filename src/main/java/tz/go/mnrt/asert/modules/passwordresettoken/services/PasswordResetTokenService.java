package tz.go.mnrt.asert.modules.passwordresettoken.services;

public interface PasswordResetTokenService {
    void sendResetLink(String userName,String userEmail,String resetLink);
}
