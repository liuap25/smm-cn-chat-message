package org.acme.chat.application.usecase;

import java.util.regex.Pattern;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MessageSanitizerService {

    // Regex para correos
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");

    // Regex para teléfonos (7 a 15 dígitos, con espacios o guiones opcionales, y prefijo + opcional)
    private static final Pattern PHONE_PATTERN =
        Pattern.compile("\\+?[0-9][0-9\\s\\-]{6,19}");

    public String sanitize(String message) {
        if (message == null || message.isBlank()) {
            return message;
        }

        // Si contiene un email o teléfono -> se enmascara TODO el mensaje
        if (EMAIL_PATTERN.matcher(message).find() || PHONE_PATTERN.matcher(message).find()) {
            return "[RESTRICTED_MESSAGE]";
        }

        // Caso contrario, se devuelve el mismo mensaje
        return message;
    }
    
}
