package com.ced.service;

import com.ced.model.MessageRequest;
import com.ced.model.User;
import com.ced.repository.UserRepository;
import com.ced.util.EmailTemplateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class EmailVerificationService {

    private final UserRepository userRepository;
    private final MessageService messageService;

    @Value("${application.verification.token.expiry-minutes:1440}")
    private int tokenExpiryMinutes;

    @Value("${application.base-url}")
    private String baseUrl;

    @Value("${application.verification.resend-hours:24}")
    private int resendIntervalHours;

    public EmailVerificationService(UserRepository userRepository, MessageService messageService) {
        this.userRepository = userRepository;
        this.messageService = messageService;
    }

    public void sendVerificationEmail(User user) {
        String token = generateVerificationToken();
        Date expiryDate = Date.from(
                LocalDateTime.now().plusMinutes(tokenExpiryMinutes)
                        .atZone(ZoneId.systemDefault()).toInstant());

        user.setVerificationToken(token);
        user.setVerificationTokenExpiry(expiryDate);
        userRepository.save(user);

        MessageRequest message = getMessageRequest(user, token);

        messageService.sendMessage(message);
    }

    private MessageRequest getMessageRequest(User user, String token) {
        String verificationLink = baseUrl + "/api/verify-email?token=" + token;

        MessageRequest message = new MessageRequest();
        message.setTo(user.getEmail());
        message.setSubject("Bem-vindo ao Cavernas e Dragões! 🐉");

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("userName", user.getName());
        placeholders.put("userEmail", user.getEmail());
        placeholders.put("verificationLink", verificationLink);
        placeholders.put("expiryHours", String.valueOf(tokenExpiryMinutes / 60));
        placeholders.put("currentYear", String.valueOf(Year.now().getValue()));

        String htmlMessage = EmailTemplateUtil.processTemplate("templates/email/welcome-verification.html", placeholders);

        message.setMessage("HTML:" + htmlMessage);
        message.setChannel("email");

        return message;
    }

    public boolean verifyEmail(String token) {
        Optional<User> userOpt = userRepository.findByVerificationToken(token);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (user.getVerificationTokenExpiry().after(new Date())) {
                user.setEmailVerified(true);
                user.setVerificationToken(null);
                user.setVerificationTokenExpiry(null);
                userRepository.save(user);
                return true;
            }
        }

        return false;
    }

    private String generateVerificationToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public int resendVerificationEmailWithStatus(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!user.isEmailVerified()) {
                if (shouldResendVerificationEmailOnLogin(user)) {
                    sendVerificationEmail(user);
                    return 1;
                } else {
                    return 0;
                }
            }
        }

        return -1;
    }

    public boolean shouldResendVerificationEmailOnLogin(User user) {
        if (user.isEmailVerified()) {
            return false;
        }

        if (user.getVerificationToken() == null) {
            return true;
        }

        Date now = new Date();
        Date tokenCreationTime = new Date(user.getVerificationTokenExpiry().getTime() - ((long) tokenExpiryMinutes * 60 * 1000));
        long diffInMillies = Math.abs(now.getTime() - tokenCreationTime.getTime());
        long diffHours = diffInMillies / (60 * 60 * 1000);

        return diffHours >= resendIntervalHours;
    }
} 