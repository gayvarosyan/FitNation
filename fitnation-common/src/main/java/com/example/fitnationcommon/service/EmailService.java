package com.example.fitnationcommon.service;

import com.example.fitnationcommon.constants.ApplicationConstants;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private static final String TEMPLATE_PATH = "static/email/trainer-invitation.html";

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public boolean sendInvitationEmail(String toEmail, String password, String loginUrl) {
        String htmlContent;
        try {
            htmlContent = buildInvitationEmailContent(toEmail, password, loginUrl);
        } catch (IOException e) {
            log.error(ApplicationConstants.LOG_EMAIL_INVITATION_TEMPLATE_LOAD_FAILED, e);
            return false;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(ApplicationConstants.EMAIL_SUBJECT_TRAINER_INVITATION);

            helper.setText(htmlContent, true);

            mailSender.send(message);
            return true;
        } catch (MessagingException e) {
            log.error(ApplicationConstants.LOG_EMAIL_INVITATION_SEND_FAILED, toEmail, e);
            return false;
        }
    }

    private String buildInvitationEmailContent(String toEmail, String password, String loginUrl)
            throws IOException {
        ClassPathResource resource = new ClassPathResource(TEMPLATE_PATH);
        String template = new String(resource.getContentAsByteArray(), StandardCharsets.UTF_8);
        return template
                .replace("{{email}}", htmlEscape(toEmail))
                .replace("{{password}}", htmlEscape(password))
                .replace("{{loginUrl}}", htmlEscape(loginUrl));
    }

    private static String htmlEscape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
