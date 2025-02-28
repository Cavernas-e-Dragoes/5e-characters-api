package com.ced.config;

import com.ced.model.Character;
import com.ced.model.User;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Configuration
public class MongoIndexConfig {

    private static final Logger logger = LoggerFactory.getLogger(MongoIndexConfig.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void initIndexes() {
        logger.info("Criando índice para Character.email");
        mongoTemplate.indexOps(Character.class).ensureIndex(new Index().on("email", ASC));
        
        logger.info("Criando índice único para User.email");
        IndexOperations userIndexOps = mongoTemplate.indexOps(User.class);
        Index emailIndex = new Index().on("email", ASC).unique();
        userIndexOps.ensureIndex(emailIndex);
    }
}