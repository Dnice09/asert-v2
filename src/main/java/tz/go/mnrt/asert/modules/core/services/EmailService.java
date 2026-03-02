package tz.go.mnrt.asert.modules.core.services;


import tz.go.mnrt.asert.modules.core.entities.EmailDetail;

public interface EmailService {

    // Method to send a simple email
    String sendSimpleMail(EmailDetail details);

    // Method to send an email with attachment
    String sendMailWithAttachment(EmailDetail details);
}
