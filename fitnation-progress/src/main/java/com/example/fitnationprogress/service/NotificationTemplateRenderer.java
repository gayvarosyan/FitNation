package com.example.fitnationprogress.service;

import com.example.fitnationprogress.dto.NotificationTriggerCommand;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class NotificationTemplateRenderer {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{\\s*([a-zA-Z0-9_]+)\\s*}}");

    public String render(String template, NotificationTriggerCommand command) {
        if (template == null) {
            return "";
        }
        Map<String, String> ctx = command.context();
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = ctx.getOrDefault(key, "");
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
