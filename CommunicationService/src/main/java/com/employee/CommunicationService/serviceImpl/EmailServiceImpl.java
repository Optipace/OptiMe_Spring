package com.employee.CommunicationService.serviceImpl;

import com.employee.CommunicationService.dto.response.ApiResponse;
import com.employee.CommunicationService.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
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

            if(body.contains("cid:leave-request-icon")){
                helper.addInline("leave-request-icon", new ClassPathResource("images/leave-request.png"));
            }

            if(body.contains("cid:leave-submitted-icon")){
                helper.addInline("leave-submitted-icon", new ClassPathResource("images/leave-confirmation.png"));
            }

            if(body.contains("cid:leave-approved-icon")){
                helper.addInline("leave-approved-icon", new ClassPathResource("images/leave-approved-icon.png"));
            }

            if(body.contains("cid:leave-rejected-icon")){
                helper.addInline("leave-rejected-icon", new ClassPathResource("images/leave-rejected-icon.png"));
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
