package com.ced.service;

import com.ced.data.DetailsUserDate;
import com.ced.dto.UserDTO;
import com.ced.exception.DuplicateEmailException;
import com.ced.model.MessageRequest;
import com.ced.model.User;
import com.ced.repository.UserRepository;
import com.mongodb.MongoWriteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final MessageService messageService;
    private final EmailVerificationService emailVerificationService;

    public UserService(final UserRepository repository, final PasswordEncoder encoder,
                       final MessageService messageService, 
                       final EmailVerificationService emailVerificationService) {
        this.repository = repository;
        this.encoder = encoder;
        this.messageService = messageService;
        this.emailVerificationService = emailVerificationService;
    }

    public UserDTO userCreate(final User user) {
        if (repository.existsByEmail(user.getEmail())) {
            logger.warn("Tentativa de criar usuário com email duplicado: {}", user.getEmail());
            throw new DuplicateEmailException(user.getEmail());
        }
        
        user.setPassword(encoder.encode(user.getPassword()));
        
        try {
            User savedUser = repository.save(user);
            logger.info("Usuário criado com sucesso: {}", savedUser.getEmail());
            
            emailVerificationService.sendVerificationEmail(savedUser);
            
            return new UserDTO(savedUser.getEmail());
        } catch (MongoWriteException e) {
            if (e.getError().getCode() == 11000 && e.getMessage().contains("email")) {
                logger.warn("Erro de e-mail duplicado (MongoWriteException): {} - {}", user.getEmail(), e.getMessage());
                throw new DuplicateEmailException(user.getEmail(), e);
            }
            logger.error("Erro ao salvar usuário (MongoWriteException): {}", e.getMessage());
            throw e;
        } catch (DuplicateKeyException e) {
            if (e.getMessage().contains("email")) {
                logger.warn("Erro de e-mail duplicado (DuplicateKeyException): {} - {}", user.getEmail(), e.getMessage());
                throw new DuplicateEmailException(user.getEmail(), e);
            }
            logger.error("Erro ao salvar usuário (DuplicateKeyException): {}", e.getMessage());
            throw e;
        }
    }

    private void sendWelcomeMail(final User user) {
        MessageRequest message = new MessageRequest();
        message.setTo(user.getEmail());
        message.setSubject("Bem-vindo ao Cavernas e Dragões!");
        message.setMessage("Olá " + user.getName() + ", sua jornada no Cavernas e Dragões começa agora! Prepare-se para aventuras épicas. Por favor, verifique seu e-mail para ativar sua conta.");
        message.setChannel("email");
        messageService.sendMessage(message);
    }

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        Optional<User> user = repository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Email " + email + " not found.");
        }
        return new DetailsUserDate(user);
    }
}