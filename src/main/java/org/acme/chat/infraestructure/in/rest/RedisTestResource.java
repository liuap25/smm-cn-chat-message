package org.acme.chat.infraestructure.in.rest;

import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.acme.shared.Cache.ChatSidebarCache;
import org.acme.shared.ChatSidebar.ChatSidebarDTO;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;



@Path("/test-redis")
public class RedisTestResource {

    private static final Logger LOG = Logger.getLogger(RedisTestResource.class.getName());

    @Inject
    ChatSidebarCache cache;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> testRedis() {
        LOG.info("🔥 Probando Redis...");

        ChatSidebarDTO sidebar = new ChatSidebarDTO(
            "chat123",
            "user456",
            "Juan Pérez",
            "foto.jpg",
            "Hola!",
            Instant.now(),
            5
        );

        return cache.save(sidebar)
            .flatMap(unused -> {
                LOG.info("✅ Guardado correctamente");

                return cache.get("chat123", "user456")
                        .onItem().invoke(result -> {
                            if (result != null) {
                                LOG.info("✅ Leído de Redis: " + result);
                            } else {
                                LOG.warning("⚠ No se encontró el sidebar en Redis");
                            }
                        })
                        .onItem().transform(result -> "✅ Prueba de Redis finalizada. Revisa la consola para detalles.");
            })
            .onFailure().invoke(fail -> LOG.log(Level.SEVERE, "❌ Error durante prueba Redis", fail));
    }
}