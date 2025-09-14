package org.acme.shared.Cache;

import java.time.Instant;

import org.acme.shared.ChatSidebar.ChatSidebarDTO;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RedisTestRunner {

    @Inject
    ChatSidebarCache cache;

    @PostConstruct
    public void runTest() {
    System.out.println("🔥 Probando Redis...");

    ChatSidebarDTO sidebar = new ChatSidebarDTO(
        "chat123",
        "user456",
        "Juan Pérez",
        "foto.jpg",
        "Hola!",
        Instant.now(),
        5
    );

    cache.save(sidebar)
        .onItem().invoke(() -> System.out.println("✅ Guardado correctamente"))
        .flatMap(v -> cache.get("chat123", "user456")) // leer después de guardar
        .subscribe().with(
            result -> {
                if (result != null) {
                    System.out.println("✅ Leído de Redis: " + result);
                } else {
                    System.out.println("⚠ No se encontró el sidebar en Redis");
                }
            },
            fail -> System.err.println("❌ Error en test de Redis: " + fail.getMessage())
        );
}
}