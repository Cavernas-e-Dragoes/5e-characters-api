package com.ced.utils;

import com.ced.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DatabaseCleanupUtil implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseCleanupUtil.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) {
        logger.info("Iniciando verificação de registros duplicados no banco de dados...");
        removeDuplicateUsers();
    }

    private void removeDuplicateUsers() {
        List<User> allUsers = mongoTemplate.findAll(User.class);
        
        Map<String, List<User>> emailToUsers = new HashMap<>();
        
        for (User user : allUsers) {
            String email = user.getEmail().toLowerCase().trim();
            emailToUsers.computeIfAbsent(email, k -> new ArrayList<>()).add(user);
        }
        
        int removedCount = 0;
        for (Map.Entry<String, List<User>> entry : emailToUsers.entrySet()) {
            List<User> users = entry.getValue();
            if (users.size() > 1) {
                logger.warn("Encontrados {} registros duplicados para o e-mail: {}", users.size(), entry.getKey());
                
                User userToKeep = findMostCompleteUser(users);
                
                for (User user : users) {
                    if (!user.equals(userToKeep)) {
                        mongoTemplate.remove(Query.query(Criteria.where("_id").is(user.getId())), User.class);
                        removedCount++;
                        logger.info("Removido registro duplicado ID: {} para e-mail: {}", user.getId(), entry.getKey());
                    }
                }
            }
        }
        
        if (removedCount > 0) {
            logger.info("Concluída limpeza de banco de dados. Removidos {} registros duplicados.", removedCount);
        } else {
            logger.info("Verificação concluída. Não foram encontrados registros duplicados.");
        }
    }
    
    private User findMostCompleteUser(List<User> users) {
        for (User user : users) {
            if (user.getVerificationToken() != null && !user.getVerificationToken().isEmpty()) {
                return user;
            }
        }
        
        return users.get(0);
    }
} 