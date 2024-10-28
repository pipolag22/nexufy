package com.example.nexufy.controller;

import com.example.nexufy.persistence.entities.Subscription;
import com.example.nexufy.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/subscription")
@Tag(name = "Subscription", description = "Gestión de suscripciones de clientes")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping
    @Operation(
            summary = "Crear suscripción",
            description = "Crea una nueva suscripción para un cliente."
    )
    @ApiResponse(responseCode = "200", description = "Suscripción creada exitosamente")
    public ResponseEntity<Subscription> createSubscription(@RequestBody Subscription subscription) {
        return ResponseEntity.ok(subscriptionService.createSubscription(subscription));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener suscripción por ID",
            description = "Devuelve una suscripción específica a partir de su ID."
    )
    @Parameter(name = "id", description = "ID de la suscripción", required = true)
    @ApiResponse(responseCode = "200", description = "Suscripción encontrada")
    @ApiResponse(responseCode = "404", description = "Suscripción no encontrada")
    public ResponseEntity<Subscription> getSubscriptionById(@PathVariable String id) {
        Optional<Subscription> subscription = subscriptionService.getSubscriptionById(id);
        return subscription.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    @Operation(
            summary = "Obtener suscripciones por cliente",
            description = "Obtiene todas las suscripciones asociadas a un cliente específico."
    )
    @Parameter(name = "customerId", description = "ID del cliente", required = true)
    @ApiResponse(responseCode = "200", description = "Lista de suscripciones encontrada")
    public List<Subscription> getSubscriptionsByCustomerId(@PathVariable String customerId) {
        return subscriptionService.getSubscriptionsByCustomerId(customerId);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar suscripción",
            description = "Actualiza los detalles de una suscripción específica."
    )
    @Parameter(name = "id", description = "ID de la suscripción a actualizar", required = true)
    @ApiResponse(responseCode = "200", description = "Suscripción actualizada exitosamente")
    @ApiResponse(responseCode = "404", description = "Suscripción no encontrada")
    public ResponseEntity<Subscription> updateSubscription(
            @PathVariable String id, @RequestBody Subscription subscription) {
        try {
            return ResponseEntity.ok(subscriptionService.updateSubscription(id, subscription));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar suscripción",
            description = "Elimina una suscripción específica a partir de su ID."
    )
    @Parameter(name = "id", description = "ID de la suscripción a eliminar", required = true)
    @ApiResponse(responseCode = "200", description = "Suscripción eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Suscripción no encontrada")
    public ResponseEntity<Void> deleteSubscription(@PathVariable String id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.ok().build();
    }
}
