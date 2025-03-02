package com.ced.controller;

import com.ced.service.EmailVerificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    public EmailVerificationController(EmailVerificationService emailVerificationService) {
        this.emailVerificationService = emailVerificationService;
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Map<String, Object>> verifyEmail(@RequestParam String token) {
        Map<String, Object> response = new HashMap<>();

        boolean verified = emailVerificationService.verifyEmail(token);

        if (verified) {
            response.put("success", true);
            response.put("message", "E-mail verificado com sucesso! Você pode fechar esta janela e fazer login.");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Link de verificação inválido ou expirado.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Map<String, Object>> resendVerification(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();

        int result = emailVerificationService.resendVerificationEmailWithStatus(email);

        if (result == 1) {
            response.put("success", true);
            response.put("message", "E-mail de verificação reenviado com sucesso!");
            return ResponseEntity.ok(response);
        } else if (result == 0) {
            response.put("success", false);
            response.put("message", "Por favor, aguarde antes de solicitar um novo e-mail de verificação.");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
        } else { 
            response.put("success", false);
            response.put("message", "Não foi possível reenviar o e-mail de verificação. Verifique se o e-mail está correto ou se já foi verificado.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
} 