package tz.go.mnrt.asert.modules.passwordresettoken.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.passwordresettoken.entity.PasswordResetToken;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl extends SimpleSearchService<PasswordResetToken> implements PasswordResetTokenService{
    private final JavaMailSender mailSender;

    @Value("${email.sender-email}")
    private String senderEmail;

    @Override
    public void sendResetLink(String userName,String userEmail, String resetLink) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(senderEmail);
        mailMessage.setTo(userEmail);
        mailMessage.setSubject("AserT: Reset Password Link");
        mailMessage.setText("Hello "+userName+",\n Please, click the link to reset your password: "+resetLink+" \n Link will expiry after one hour(60minutes).\n\nRegards,\nAserT support team");
        try {
            mailSender.send(mailMessage);
            System.out.println("Email sent successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
