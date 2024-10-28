package com.example.nexufy.controller;

import com.example.nexufy.dtos.CustomerDTO;
import com.example.nexufy.persistence.entities.Customer;
import com.example.nexufy.persistence.entities.Product;
import com.example.nexufy.persistence.entities.RatingComment;
import com.example.nexufy.persistence.repository.CustomerRepository;
import com.example.nexufy.persistence.repository.ProductRepository;
import com.example.nexufy.persistence.repository.RatingCommentRepository;
import com.example.nexufy.payload.response.MessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/superadmin/customers")
@Tag(name = "SuperAdmin - Gestión de Clientes", description = "Operaciones para gestionar clientes, productos y comentarios")
@PreAuthorize("hasRole('SUPERADMIN')")
public class SuperAdminCustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RatingCommentRepository ratingCommentRepository;

    @PostMapping("/create")
    @Operation(summary = "Crear cliente", description = "Permite al SUPERADMIN crear un nuevo cliente")
    @ApiResponse(responseCode = "200", description = "Cliente creado exitosamente")
    @ApiResponse(responseCode = "400", description = "El nombre de usuario o correo ya están en uso")
    public ResponseEntity<?> createCustomer(@RequestBody Customer customer) {
        if (customerRepository.existsByUsername(customer.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }
        if (customerRepository.existsByEmail(customer.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }
        customerRepository.save(customer);
        return ResponseEntity.ok(new MessageResponse("Customer created successfully!"));
    }

    @PutMapping("/edit/{id}")
    @Operation(summary = "Editar cliente", description = "Actualiza los datos de un cliente")
    @Parameter(name = "id", description = "ID del cliente a actualizar", required = true)
    @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    public ResponseEntity<?> updateCustomer(@PathVariable String id, @RequestBody Customer updatedCustomer) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setUsername(updatedCustomer.getUsername());
        customer.setEmail(updatedCustomer.getEmail());
        customer.setPassword(updatedCustomer.getPassword());

        customerRepository.save(customer);
        return ResponseEntity.ok(new MessageResponse("Customer updated successfully!"));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Eliminar cliente", description = "Elimina un cliente junto con sus productos")
    @Parameter(name = "id", description = "ID del cliente a eliminar", required = true)
    @ApiResponse(responseCode = "200", description = "Cliente y productos eliminados exitosamente")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    public ResponseEntity<?> deleteCustomer(@PathVariable String id) {
        Optional<Customer> customerOpt = customerRepository.findById(id);
        if (!customerOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Customer not found"));
        }
        Customer customer = customerOpt.get();
        List<Product> products = productRepository.findByCustomerId(customer.getId());
        productRepository.deleteAll(products);
        customerRepository.delete(customer);
        return ResponseEntity.ok(new MessageResponse("Customer and their products deleted successfully!"));
    }

    @PutMapping("/suspend/{id}")
    @Operation(summary = "Suspender cliente", description = "Suspende un cliente por un número determinado de días")
    @Parameter(name = "id", description = "ID del cliente a suspender", required = true)
    @Parameter(name = "days", description = "Número de días de suspensión", required = true)
    @ApiResponse(responseCode = "200", description = "Cliente suspendido exitosamente")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    public ResponseEntity<?> suspendCustomer(@PathVariable String id, @RequestParam int days) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setSuspended(true);
        customer.setSuspendedUntil(LocalDateTime.now().plusDays(days));
        customerRepository.save(customer);
        return ResponseEntity.ok(new MessageResponse("Customer suspended for " + days + " days!"));
    }

    @PutMapping("/unsuspend/{id}")
    @Operation(summary = "Levantar suspensión", description = "Remueve la suspensión de un cliente")
    @Parameter(name = "id", description = "ID del cliente", required = true)
    @ApiResponse(responseCode = "200", description = "Suspensión removida")
    public ResponseEntity<?> unsuspendCustomer(@PathVariable String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setSuspended(false);
        customer.setSuspendedUntil(null);
        customerRepository.save(customer);
        return ResponseEntity.ok(new MessageResponse("Customer suspension removed!"));
    }

    @GetMapping("/{id}/products")
    @Operation(summary = "Obtener productos", description = "Obtiene los productos de un cliente específico")
    @ApiResponse(responseCode = "200", description = "Productos obtenidos")
    public ResponseEntity<List<Product>> getCustomerProducts(@PathVariable String id) {
        List<Product> products = productRepository.findByCustomerId(id);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/product/suspend/{productId}")
    @Operation(summary = "Suspender producto", description = "Suspende un producto")
    @Parameter(name = "productId", description = "ID del producto", required = true)
    @ApiResponse(responseCode = "200", description = "Producto suspendido")
    public ResponseEntity<?> suspendProduct(@PathVariable String productId, @RequestParam int days) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setSuspended(true);
        product.setSuspendedUntil(LocalDateTime.now().plusDays(days));
        productRepository.save(product);
        return ResponseEntity.ok(new MessageResponse("Product suspended for " + days + " days!"));
    }

    @DeleteMapping("/product/{productId}")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto")
    @ApiResponse(responseCode = "200", description = "Producto eliminado")
    public ResponseEntity<?> deleteProduct(@PathVariable String productId) {
        productRepository.deleteById(productId);
        return ResponseEntity.ok(new MessageResponse("Product deleted successfully!"));
    }
}
