package com.medicinelocator.auth.infrastructure.email;

import com.medicinelocator.auth.application.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SmtpEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailService.class);

    private final JavaMailSender mailSender;
    private final EmailTemplates emailTemplates;

    public SmtpEmailService(JavaMailSender mailSender, EmailTemplates emailTemplates) {
        this.mailSender = mailSender;
        this.emailTemplates = emailTemplates;
    }

    @Async
    @Override
    public void sendEmailVerification(String to, String token) {
        sendHtmlEmail(
                to,
                emailTemplates.verificationSubject(),
                emailTemplates.verificationBody(token)
        );
    }

    @Async
    @Override
    public void sendPasswordReset(String to, String token) {
        sendHtmlEmail(
                to,
                emailTemplates.passwordResetSubject(),
                emailTemplates.passwordResetBody(token)
        );
    }

    @Async
    @Override
    public void sendWelcomeEmail(String to, String name) {
        sendHtmlEmail(
                to,
                emailTemplates.welcomeSubject(),
                emailTemplates.welcomeBody(name)
        );
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent to: {} subject: {}", to, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {} error: {}", to, e.getMessage(), e);
        }
    }
}