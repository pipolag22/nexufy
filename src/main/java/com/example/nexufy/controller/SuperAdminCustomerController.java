package com.example.nexufy.controller;

import com.example.nexufy.dtos.CustomerDTO;
import com.example.nexufy.persistence.entities.Customer;
import com.example.nexufy.persistence.entities.Product;
import com.example.nexufy.persistence.entities.RatingComment;
import com.example.nexufy.persistence.repository.CustomerRepository;
import com.example.nexufy.persistence.repository.ProductRepository;
import com.example.nexufy.persistence.repository.RatingCommentRepository;
import com.example.nexufy.payload.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/superadmin/customers")
@PreAuthorize("hasRole('SUPERADMIN')")
public class SuperAdminCustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RatingCommentRepository ratingCommentRepository;

    // Crear un nuevo cliente
    @PostMapping("/create")
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

    // Editar un cliente
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable String id, @RequestBody Customer updatedCustomer) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setUsername(updatedCustomer.getUsername());
        customer.setEmail(updatedCustomer.getEmail());
        customer.setPassword(updatedCustomer.getPassword());

        customerRepository.save(customer);
        return ResponseEntity.ok(new MessageResponse("Customer updated successfully!"));
    }

    // Eliminar un cliente y sus productos
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable String id) {
        Optional<Customer> customerOpt = customerRepository.findById(id);
        if (!customerOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Customer not found"));
        }

        Customer customer = customerOpt.get();
        List<Product> products = productRepository.findByCustomerId(customer.getId());

        // Eliminar productos del cliente
        productRepository.deleteAll(products);

        // Eliminar al cliente
        customerRepository.delete(customer);
        return ResponseEntity.ok(new MessageResponse("Customer and their products deleted successfully!"));
    }

    // Suspender un cliente por un tiempo determinado
    @PutMapping("/suspend/{id}")
    public ResponseEntity<?> suspendCustomer(@PathVariable String id, @RequestParam int days) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        LocalDateTime suspensionEnd = LocalDateTime.now().plusDays(days);
        customer.setSuspended(true);
        customer.setSuspendedUntil(suspensionEnd);
        customer.setSuspendedReason("Suspended for " + days + " days");

        customerRepository.save(customer);
        return ResponseEntity.ok(new MessageResponse("Customer suspended for " + days + " days!"));
    }

    // Retirar suspensión de un cliente
    @PutMapping("/unsuspend/{id}")
    public ResponseEntity<?> unsuspendCustomer(@PathVariable String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setSuspended(false);
        customer.setSuspendedUntil(null);
        customer.setSuspendedReason(null);

        customerRepository.save(customer);
        return ResponseEntity.ok(new MessageResponse("Customer suspension removed!"));
    }

    // Gestionar productos de un cliente
    @GetMapping("/{id}/products")
    public ResponseEntity<List<Product>> getCustomerProducts(@PathVariable String id) {
        List<Product> products = productRepository.findByCustomerId(id);
        return ResponseEntity.ok(products);
    }

    // Suspender un producto
    @PutMapping("/product/suspend/{productId}")
    public ResponseEntity<?> suspendProduct(@PathVariable String productId, @RequestParam int days) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        LocalDateTime suspensionEnd = LocalDateTime.now().plusDays(days);
        product.setSuspended(true);
        product.setSuspendedUntil(suspensionEnd);
        product.setSuspendedReason("Suspended for " + days + " days");

        productRepository.save(product);
        return ResponseEntity.ok(new MessageResponse("Product suspended for " + days + " days!"));
    }

    // Retirar suspensión de un producto
    @PutMapping("/product/unsuspend/{productId}")
    public ResponseEntity<?> unsuspendProduct(@PathVariable String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setSuspended(false);
        product.setSuspendedUntil(null);
        product.setSuspendedReason(null);

        productRepository.save(product);
        return ResponseEntity.ok(new MessageResponse("Product suspension removed!"));
    }

    // Eliminar producto
    @DeleteMapping("/product/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable String productId) {
        productRepository.deleteById(productId);
        return ResponseEntity.ok(new MessageResponse("Product deleted successfully!"));
    }

    // Obtener comentarios de un producto
    @GetMapping("/product/{productId}/comments")
    public ResponseEntity<List<RatingComment>> getProductComments(@PathVariable String productId) {
        List<RatingComment> comments = ratingCommentRepository.findByProductId(productId);
        return ResponseEntity.ok(comments);
    }

    // Eliminar comentario
    @DeleteMapping("/product/comment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable String commentId) {
        ratingCommentRepository.deleteById(commentId);
        return ResponseEntity.ok(new MessageResponse("Comment deleted successfully!"));
    }

    // Suspender comentario
    @PutMapping("/product/comment/suspend/{commentId}")
    public ResponseEntity<?> suspendComment(@PathVariable String commentId) {
        RatingComment comment = ratingCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.setText("This comment has been suspended.");
        ratingCommentRepository.save(comment);
        return ResponseEntity.ok(new MessageResponse("Comment suspended successfully!"));
    }
}
