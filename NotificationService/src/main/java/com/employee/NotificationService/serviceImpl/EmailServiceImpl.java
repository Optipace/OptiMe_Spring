package com.employee.NotificationService.serviceImpl;

import com.employee.NotificationService.dto.response.ApiResponse;
import com.employee.NotificationService.exception.CustomException;
import com.employee.NotificationService.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public ApiResponse<String> sendHtmlEmail(String to, String subject, String body) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try{
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            // Only attach the image if the HTML template actually asks for it!
            if (body.contains("cid:logo")) {
                helper.addInline("logo", new ClassPathResource("images/optipace.png"));
            }

            if (body.contains("cid:account-created")) {
                helper.addInline("account-created", new ClassPathResource("images/account-created.png"));
            }

            if (body.contains("cid:registration-completed")) {
                helper.addInline("registration-completed", new ClassPathResource("images/registration-completed.png"));
            }

            mailSender.send(mimeMessage);
            log.info("Email sent successfully to {}",to);
            return new ApiResponse<>(
                    true,
                    "Email sent successfully to "+to,
                    null,
                    LocalDateTime.now(),
                    200
            );
        } catch (Exception e){
            log.error("Email sending failed ",e);
            return new ApiResponse<>(
                    false,
                    "Email sending failed",
                    null,
                    LocalDateTime.now(),
                    500
            );
        }
    }
}
