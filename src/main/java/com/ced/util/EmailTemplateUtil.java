package com.ced.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailTemplateUtil {

    private static final Logger logger = LoggerFactory.getLogger(EmailTemplateUtil.class);
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");

    public static String processTemplate(String templatePath, Map<String, String> placeholders) {
        String template = loadTemplate(templatePath);
        return replacePlaceholders(template, placeholders);
    }

    private static String loadTemplate(String templatePath) {
        try {
            Resource resource = new ClassPathResource(templatePath);
            Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            logger.error("Erro ao carregar template de email: {}", templatePath, e);
            throw new RuntimeException("Não foi possível carregar o template de email", e);
        }
    }

    private static String replacePlaceholders(String template, Map<String, String> placeholders) {
        if (template == null || placeholders == null) {
            return template;
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            String placeholder = matcher.group(1);
            String replacement = placeholders.getOrDefault(placeholder, "");
            replacement = Matcher.quoteReplacement(replacement);
            matcher.appendReplacement(buffer, replacement);
        }
        matcher.appendTail(buffer);

        return buffer.toString();
    }
}