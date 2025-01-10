package com.cryptfolio.userservice.Service;

import com.cryptfolio.userservice.Entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    @Value("${support.email}")
    private String senderEmail;


    public void sendEmail(MimeMessage message) {
        mailSender.send(message);
    }

    public MimeMessage constructResetTokenEmail(String frontendUrl, String token, User user) throws MessagingException {
        String resetUrl = frontendUrl + "/reset-form?token=" + token;

        // Create the HTML content for the email
        String message = "<h1>Password Reset Request</h1>"
                + "<h2>Hi " + user.getFirstName() + "</h2>"
                + "<h3>We received a request to reset your password. Click the link below to reset it:</h3>"
                + "<a href=\"" + resetUrl + "\">Reset Password</a>"
                + "<p>If you didn’t ask to reset your password, you can ignore this email.</p>"
                + "<p>Thanks,</p>";

        return constructEmail("Reset Password", message, user);
    }

    public MimeMessage constructEmail(String subject, String body, User user) throws MessagingException {
        MimeMessage email = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(email, true, "UTF-8");

        helper.setSubject(subject);
        helper.setText(body, true); // Pass 'true' to indicate that the body is HTML
        helper.setTo(user.getEmail());
        helper.setFrom(senderEmail);
        return email;
    }
}
