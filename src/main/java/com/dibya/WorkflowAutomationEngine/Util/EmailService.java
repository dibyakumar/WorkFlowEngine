package com.dibya.WorkflowAutomationEngine.Util;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private  JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String userEmail;

    public boolean sendEmail(String to, String subject, String htmlContent) {

        try{
            // we can use this whenever we want to send a simple text email .
            //  SimpleMailMessage message = new SimpleMailMessage();
            // if you want to send as a HTML text you can use mimeMa ilMessage
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");
            helper.setFrom(userEmail);  // Sender's email
            helper.setTo(to);                        // Recipient's email
            helper.setSubject(subject);              // Email subject
            helper.setText(htmlContent,true);                    // Email body (content)

            javaMailSender.send(message);
            return true;
        }catch (Exception e) {
            return false;
        }
    }
}
